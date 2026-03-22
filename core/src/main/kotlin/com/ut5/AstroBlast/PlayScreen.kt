package com.ut5.AstroBlast

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array as GdxArray
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlin.math.sqrt

// Pantalla principal con toda la lógica del juego.
// Sigo la misma estructura que Drop: render -> entrada -> logica -> dibujar
class PlayScreen(private val juego: GameMain) : Screen {

    private val viewportJuego = FitViewport(8f, 5f)
    private val viewportUI    = ScreenViewport()

    private val textureFondo:        Texture = TextureFactory.makeFondo(800, 500)
    private val textureNave:         Texture = TextureFactory.makeNave(64, 80)
    private val texturaPiedra:       Texture = TextureFactory.makePiedra(48, 48)
    private val texturaPiedraGrande: Texture = TextureFactory.makePiedraGrande(64, 64)
    private val texturaCristal:      Texture = TextureFactory.makeCristal(40, 40)
    private val texturaBarraFondo:   Texture = TextureFactory.makeColorSolido(1, 1, Color(0.08f, 0.1f, 0.22f, 1f))
    private val texturaBarraLleno:   Texture = TextureFactory.makeColorSolido(1, 1, Color(0.18f, 0.85f, 1f, 1f))
    private val texturaPanelHUD:     Texture = TextureFactory.makeColorSolido(1, 1, Color(0f, 0.03f, 0.12f, 0.88f))

    private val sonidoCristal: Sound = AudioFactory.sonidoCristal()
    private val sonidoImpacto: Sound = AudioFactory.sonidoImpacto()
    private val musicaFondo:   Music = AudioFactory.musicaAmbiente()

    private val spriteNave = Sprite(textureNave).apply {
        setSize(0.75f, 0.94f)
        setPosition(4f - 0.375f, 0.15f)
    }

    private val posicionToque = Vector2()

    private val spritesObjetos = GdxArray<Sprite>()
    private val tiposObjetos   = GdxArray<EntityType>()

    private var timerSpawn = 0f

    private val rectNave   = Rectangle()
    private val rectObjeto = Rectangle()

    private var escudo         = 100f
    private var tiempoRestante = 90f
    private var puntuacion     = 0
    private var tiempoFlash    = 0f

    // imán: atrae los cristales hacia la nave durante 3 segundos
    private var imanActivo      = false
    private var timerIman       = 0f   // tiempo que le queda al imán
    private var cooldownIman    = 0f   // tiempo hasta que se puede volver a usar

    override fun show() {
        musicaFondo.isLooping = true
        musicaFondo.volume    = 0.4f
        musicaFondo.play()
    }

    override fun render(delta: Float) {
        entrada()
        logica(delta)
        dibujar()
    }

    private fun entrada() {
        val velocidad = 4.5f
        val deltaTime = Gdx.graphics.deltaTime

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            spriteNave.translateX(velocidad * deltaTime)
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
            spriteNave.translateX(-velocidad * deltaTime)

        if (Gdx.input.isTouched()) {
            posicionToque.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            viewportJuego.unproject(posicionToque)
            spriteNave.setCenterX(posicionToque.x)
        }

        // E activa el imán si no está en cooldown
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && cooldownIman <= 0f) {
            imanActivo   = true
            timerIman    = 3f
            cooldownIman = 8f
        }
    }

    private fun logica(delta: Float) {
        val anchoPantalla = viewportJuego.worldWidth
        val altoPantalla  = viewportJuego.worldHeight

        tiempoRestante -= delta
        if (tiempoRestante <= 0f) {
            musicaFondo.stop()
            juego.setScreen(GameOverScreen(juego, victoria = true, puntuacion = puntuacion))
            dispose(); return
        }

        spriteNave.x = MathUtils.clamp(spriteNave.x, 0f, anchoPantalla - spriteNave.width)

        escudo = MathUtils.clamp(escudo, 0f, 100f)
        if (escudo <= 0f) {
            musicaFondo.stop()
            juego.setScreen(GameOverScreen(juego, victoria = false, puntuacion = puntuacion))
            dispose(); return
        }

        if (tiempoFlash > 0f) tiempoFlash -= delta

        // actualizar timers del imán
        if (imanActivo) {
            timerIman -= delta
            if (timerIman <= 0f) imanActivo = false
        }
        if (cooldownIman > 0f) cooldownIman -= delta

        val tiempoJugado  = 90f - tiempoRestante
        val velocidadBase = 2.0f + tiempoJugado * 0.015f

        rectNave.set(spriteNave.x + 0.05f, spriteNave.y, spriteNave.width - 0.1f, spriteNave.height * 0.7f)

        for (i in spritesObjetos.size - 1 downTo 0) {
            val sprite = spritesObjetos[i]
            val tipo   = tiposObjetos[i]
            val ancho  = sprite.width
            val alto   = sprite.height

            // si el imán está activo los cristales se mueven hacia la nave
            if (imanActivo && tipo == EntityType.CRYSTAL) {
                val centroNaveX = spriteNave.x + spriteNave.width / 2f
                val centroNaveY = spriteNave.y + spriteNave.height / 2f
                val dx = centroNaveX - (sprite.x + ancho / 2f)
                val dy = centroNaveY - (sprite.y + alto  / 2f)
                val distancia = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                if (distancia > 0.01f)
                {
                    sprite.translateX(dx / distancia * 6f * delta)
                    sprite.translateY(dy / distancia * 6f * delta)
                }
            } else
            {
                val velocidad = when (tipo)
                {
                    EntityType.CRYSTAL  -> velocidadBase * 0.65f
                    EntityType.ROCK     -> velocidadBase
                    EntityType.BIGROCK  -> velocidadBase * 1.30f
                }
                sprite.translateY(-velocidad * delta)
            }

            rectObjeto.set(sprite.x + ancho * 0.1f, sprite.y + alto * 0.1f, ancho * 0.8f, alto * 0.8f)

            when {
                sprite.y < -alto -> {
                    spritesObjetos.removeIndex(i); tiposObjetos.removeIndex(i)
                }
                rectNave.overlaps(rectObjeto) -> {
                    when (tipo) {
                        EntityType.CRYSTAL -> { escudo += 20f; puntuacion++; sonidoCristal.play() }
                        EntityType.ROCK    -> { escudo -= 20f; tiempoFlash = 0.4f;  sonidoImpacto.play() }
                        EntityType.BIGROCK -> { escudo -= 35f; tiempoFlash = 0.65f; sonidoImpacto.play() }
                    }
                    spritesObjetos.removeIndex(i); tiposObjetos.removeIndex(i)
                }
            }
        }

        val intervaloSpawn = (2.0f - tiempoJugado * 0.007f).coerceAtLeast(0.55f)
        timerSpawn += delta
        if (timerSpawn >= intervaloSpawn) {
            timerSpawn = 0f
            generarObjeto(tiempoJugado, anchoPantalla, altoPantalla)
        }
    }

    private fun generarObjeto(tiempoJugado: Float, anchoPantalla: Float, altoPantalla: Float) {
        val tipo = if (tiempoJugado < 30f)
        {
            if (MathUtils.random() < 0.30f) EntityType.CRYSTAL else EntityType.ROCK
        } else
        {
            val probabilidad = MathUtils.random()
            when {
                probabilidad < 0.28f -> EntityType.CRYSTAL
                probabilidad < 0.65f -> EntityType.ROCK
                else                 -> EntityType.BIGROCK
            }
        }

        val (textura, tamanyo) = when (tipo)
        {
            EntityType.CRYSTAL  -> Pair(texturaCristal,      0.55f)
            EntityType.ROCK     -> Pair(texturaPiedra,       0.65f)
            EntityType.BIGROCK  -> Pair(texturaPiedraGrande, 0.85f)
        }

        spritesObjetos.add(Sprite(textura).apply {
            setSize(tamanyo, tamanyo)
            x = MathUtils.random(0f, anchoPantalla - tamanyo)
            y = altoPantalla
        })
        tiposObjetos.add(tipo)
    }

    private fun dibujar()
    {
        ScreenUtils.clear(Color.BLACK)

        viewportJuego.apply()
        juego.batch.setProjectionMatrix(viewportJuego.camera.combined)
        juego.batch.begin()
        juego.batch.setColor(1f, 1f, 1f, 1f)
        juego.batch.draw(textureFondo, 0f, 0f, viewportJuego.worldWidth, viewportJuego.worldHeight)
        spritesObjetos.forEach { it.draw(juego.batch) }
        // la nave parpadea en verde cuando el imán está activo
        if (imanActivo) juego.batch.setColor(0.3f, 1f, 0.6f, 1f)
        else            juego.batch.setColor(1f, 1f, 1f, 1f)
        spriteNave.draw(juego.batch)
        juego.batch.setColor(1f, 1f, 1f, 1f)
        juego.batch.end()

        viewportUI.apply()
        juego.batch.setProjectionMatrix(viewportUI.camera.combined)
        juego.batch.begin()

        val anchoPantalla = viewportUI.screenWidth.toFloat()
        val altoPantalla  = viewportUI.screenHeight.toFloat()

        juego.batch.setColor(1f, 1f, 1f, 1f)
        juego.batch.draw(texturaPanelHUD, 0f, altoPantalla - 38f, anchoPantalla, 38f)

        juego.batch.draw(texturaBarraFondo, 10f, altoPantalla - 28f, 180f, 14f)
        when {
            escudo < 25f -> juego.batch.setColor(0.95f, 0.15f, 0.1f, 1f)
            escudo < 55f -> juego.batch.setColor(1f,    0.62f, 0.08f, 1f)
            else -> juego.batch.setColor(0.18f, 0.85f, 1f,   1f)
        }
        juego.batch.draw(texturaBarraLleno, 10f, altoPantalla - 28f, 180f * (escudo / 100f), 14f)
        juego.batch.setColor(1f, 1f, 1f, 1f)

        juego.fuente.setColor(Color(0.8f, 0.92f, 1f, 1f))
        juego.fuente.draw(juego.batch, "Escudo ${escudo.toInt()}%", 10f, altoPantalla - 32f)

        val textoTiempo  = "%d:%02d".format((tiempoRestante / 60).toInt(), (tiempoRestante % 60).toInt())
        juego.fuente.setColor(if (tiempoRestante < 20f) Color(1f, 0.3f, 0.3f, 1f) else Color(0.9f, 0.95f, 1f, 1f))
        val medidaTiempo = GlyphLayout(juego.fuente, textoTiempo)
        juego.fuente.draw(juego.batch, textoTiempo, (anchoPantalla - medidaTiempo.width) / 2f, altoPantalla - 12f)

        juego.fuente.setColor(Color(0.22f, 1f, 0.62f, 1f))
        juego.fuente.draw(juego.batch, "Cristales: $puntuacion", anchoPantalla - 145f, altoPantalla - 12f)

        // indicador del imán abajo a la izquierda
        val textoIman = when {

            imanActivo  -> "E - IMAN: ${timerIman.toInt() + 1}s"
            cooldownIman > 0 -> "E - recarga: ${cooldownIman.toInt() + 1}s"
            else  -> "E - IMAN"
        }
        juego.fuente.setColor(when {
            imanActivo  -> Color(0.3f, 1f, 0.6f, 1f)
            cooldownIman > 0 -> Color(0.5f, 0.5f, 0.5f, 1f)
            else  -> Color(0.9f, 0.85f, 0.5f, 1f)
        })
        juego.fuente.draw(juego.batch, textoIman, 10f, 30f)

        if (tiempoFlash > 0f) {
            juego.batch.setColor(0.9f, 0.05f, 0.05f, tiempoFlash * 0.25f)
            juego.batch.draw(texturaBarraFondo, 0f, 0f, anchoPantalla, altoPantalla)
            juego.batch.setColor(1f, 1f, 1f, 1f)
        }

        juego.batch.end()
    }

    override fun resize(ancho: Int, alto: Int) {
        viewportJuego.update(ancho, alto, true)
        viewportUI.update(ancho, alto, true)
    }
    override fun pause()  { musicaFondo.pause() }
    override fun resume() { musicaFondo.play()  }
    override fun hide()   { musicaFondo.stop()  }

    override fun dispose() {
        textureFondo.dispose(); textureNave.dispose()
        texturaPiedra.dispose(); texturaPiedraGrande.dispose(); texturaCristal.dispose()
        texturaBarraFondo.dispose(); texturaBarraLleno.dispose(); texturaPanelHUD.dispose()
        sonidoCristal.dispose(); sonidoImpacto.dispose(); musicaFondo.dispose()
    }
}
