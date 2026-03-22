package com.ut5.AstroBlast

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlin.math.abs
import kotlin.math.sin

// Primera pantalla que ve el jugador. Muestra las  instrucciones
class MenuScreen(private val juego: GameMain) : Screen {

    private val viewportJuego = FitViewport(8f, 5f)
    private val viewportUI    = ScreenViewport()

    private val textureFondo   = TextureFactory.makeFondo(800, 500)
    private val textureNave    = TextureFactory.makeNave(64, 80)
    private val texturaPiedra  = TextureFactory.makePiedra(40, 40)
    private val texturaCristal = TextureFactory.makeCristal(40, 40)
    private val texturaPanel   = TextureFactory.makeColorSolido(1, 1, Color(0f, 0.05f, 0.15f, 0.72f))

    override fun show() {}

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)

        // primero dibujo los sprites con el viewport del juego (8x5 unidades)
        viewportJuego.apply()
        juego.batch.setProjectionMatrix(viewportJuego.camera.combined)
        juego.batch.begin()
        val anchoMundo = viewportJuego.worldWidth
        val altoMundo  = viewportJuego.worldHeight
        juego.batch.draw(textureFondo,   0f,    0f,    anchoMundo, altoMundo)
        juego.batch.draw(texturaPanel,   1.3f,  0.4f,  5.4f, 3.8f)
        juego.batch.draw(textureNave,    0.1f,  1.6f,  0.9f, 1.1f)
        juego.batch.draw(texturaPiedra,  1.65f, 2.3f,  0.5f, 0.5f)
        juego.batch.draw(texturaCristal, 1.65f, 1.65f, 0.5f, 0.5f)
        juego.batch.end()

        // luego el texto con el viewport de UI en píxeles para que se vea nítido
        viewportUI.apply()
        juego.batch.setProjectionMatrix(viewportUI.camera.combined)
        juego.batch.begin()

        val anchoPantalla = viewportUI.screenWidth.toFloat()
        val altoPantalla  = viewportUI.screenHeight.toFloat()

        juego.fuente.setColor(Color(0.35f, 0.88f, 1f, 1f))
        val medidaTitulo = GlyphLayout(juego.fuente, "ASTROBLAST")
        juego.fuente.draw(juego.batch, "ASTROBLAST", (anchoPantalla - medidaTitulo.width) / 2f, altoPantalla - 20f)

        juego.fuente.setColor(Color(0.6f, 0.78f, 0.95f, 1f))
        val textoSub     = "Esquiva rocas  |  Recoge cristales  |  Sobrevive 90s"
        val medidaSub    = GlyphLayout(juego.fuente, textoSub)
        juego.fuente.draw(juego.batch, textoSub, (anchoPantalla - medidaSub.width) / 2f, altoPantalla - 50f)

        juego.fuente.setColor(Color(0.9f, 0.95f, 1f, 1f))
        juego.fuente.draw(juego.batch, "CONTROLES:", anchoPantalla * 0.28f, altoPantalla - 95f)
        juego.fuente.setColor(Color(0.72f, 0.82f, 0.92f, 1f))
        juego.fuente.draw(juego.batch, "A / D  o  flechas  ->  mover la nave", anchoPantalla * 0.28f, altoPantalla - 118f)
        juego.fuente.draw(juego.batch, "Click / toque   ->  ir al punto",   anchoPantalla * 0.28f, altoPantalla - 140f)
        juego.fuente.draw(juego.batch, "E  -> Obtienes un iman 3s (recarga 8s)", anchoPantalla * 0.28f, altoPantalla - 162f)

        juego.fuente.setColor(Color(0.22f, 0.42f, 0.65f, 1f))
        juego.fuente.draw(juego.batch, "- - - - - - - - - - - - - - - - - - - - - - - -", anchoPantalla * 0.28f, altoPantalla - 184f)

        juego.fuente.setColor(Color(0.85f, 0.68f, 0.55f, 1f))
        juego.fuente.draw(juego.batch, "Meteorito pequeño  ->  -20 escudo", anchoPantalla * 0.33f, altoPantalla - 217f)
        juego.fuente.setColor(Color(0.25f, 1f, 0.65f, 1f))
        juego.fuente.draw(juego.batch, "Cristal verde   ->  +20 escudo", anchoPantalla * 0.33f, altoPantalla - 240f)
        juego.fuente.setColor(Color(1f, 0.48f, 0.22f, 1f))
        juego.fuente.draw(juego.batch, "Meteorito grande  ->  -35 escudo  (aparece a los 30s)", anchoPantalla * 0.28f, altoPantalla - 263f)

        juego.fuente.setColor(Color(0.9f, 0.85f, 0.5f, 1f))
        juego.fuente.draw(juego.batch, "Objetivo: sobrevive 90 segundos con el escudo > 0", anchoPantalla * 0.28f, altoPantalla - 290f)

        // texto parpadeante usando seno del tiempo
        val brillo = abs(sin(System.currentTimeMillis() / 500.0)).toFloat()
        juego.fuente.setColor(Color(0.4f, 0.9f, 1f, 0.35f + brillo * 0.65f))
        val textoEmpezar = "- Pulsa ENTER o ESPACIO para empezar -"
        val medidaEmpezar = GlyphLayout(juego.fuente, textoEmpezar)
        juego.fuente.draw(juego.batch, textoEmpezar, (anchoPantalla - medidaEmpezar.width) / 2f, 45f)

        juego.batch.end()

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
         || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
         || Gdx.input.justTouched()) {
            juego.setScreen(PlayScreen(juego)); dispose()
        }
    }

    override fun resize(ancho: Int, alto: Int) {
        viewportJuego.update(ancho, alto, true)
        viewportUI.update(ancho, alto, true)
    }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}

    override fun dispose() {
        textureFondo.dispose(); textureNave.dispose()
        texturaPiedra.dispose(); texturaCristal.dispose(); texturaPanel.dispose()
    }
}
