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

// Pantalla de resultado: aparece cuando ganas (90s) o pierdes (escudo a 0)
class GameOverScreen(
    private val juego:     GameMain,
    private val victoria:  Boolean,
    private val puntuacion: Int
) : Screen {

    private val viewportJuego = FitViewport(8f, 5f)
    private val viewportUI    = ScreenViewport()

    private val textureFondo   = TextureFactory.makeFondo(800, 500)
    private val textureNave    = TextureFactory.makeNave(64, 80)
    private val texturaOverlay = TextureFactory.makeColorSolido(1, 1, Color(0f, 0.02f, 0.1f, 0.75f))
    private val texturaPanel   = TextureFactory.makeColorSolido(1, 1, Color(0f, 0.05f, 0.18f, 0.88f))

    override fun show() {}

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)

        // fondo y nave con el viewport del juego
        viewportJuego.apply()
        juego.batch.setProjectionMatrix(viewportJuego.camera.combined)
        juego.batch.begin()

        val anchoMundo = viewportJuego.worldWidth
        val altoMundo  = viewportJuego.worldHeight

        juego.batch.setColor(1f, 1f, 1f, 1f)
        juego.batch.draw(textureFondo,   0f, 0f, anchoMundo, altoMundo)
        juego.batch.draw(texturaOverlay, 0f, 0f, anchoMundo, altoMundo)

        // si perdiste la nave aparece semitransparente en la pantalla final
        juego.batch.setColor(1f, 1f, 1f, if (victoria) 1f else 0.4f)
        juego.batch.draw(textureNave, anchoMundo / 2f - 0.42f, 0.05f, 0.85f, 1.06f)
        juego.batch.setColor(1f, 1f, 1f, 1f)
        juego.batch.end()

        // texto con el viewport de UI en píxeles
        viewportUI.apply()
        juego.batch.setProjectionMatrix(viewportUI.camera.combined)
        juego.batch.begin()

        val anchoPantalla = viewportUI.screenWidth.toFloat()
        val altoPantalla  = viewportUI.screenHeight.toFloat()

        juego.batch.draw(texturaPanel, anchoPantalla * 0.15f, altoPantalla * 0.25f, anchoPantalla * 0.70f, altoPantalla * 0.60f)

        juego.fuente.setColor(if (victoria) Color(0.28f, 1f, 0.68f, 1f) else Color(1f, 0.25f, 0.18f, 1f))
        val textoTitulo  = if (victoria) "MISIÓN CUMPLIDA" else "NAVE DESTRUIDA"
        val medidaTitulo = GlyphLayout(juego.fuente, textoTitulo)

        juego.fuente.draw(juego.batch, textoTitulo, (anchoPantalla - medidaTitulo.width) / 2f, altoPantalla * 0.82f)
        juego.fuente.setColor(Color(0.65f, 0.78f, 0.95f, 1f))

        val textoSub  = if (victoria) "Cruzaste el cinturón de asteroides." else "El escudo no aguantó el impacto."
        val medidaSub = GlyphLayout(juego.fuente, textoSub)

        juego.fuente.draw(juego.batch, textoSub, (anchoPantalla - medidaSub.width) / 2f, altoPantalla * 0.72f)

        juego.fuente.setColor(Color(0.2f, 1f, 0.62f, 1f))
        val textoPuntuacion  = "Cristales recogidos: $puntuacion"
        val medidaPuntuacion = GlyphLayout(juego.fuente, textoPuntuacion)
        juego.fuente.draw(juego.batch, textoPuntuacion, (anchoPantalla - medidaPuntuacion.width) / 2f, altoPantalla * 0.62f)

        juego.fuente.setColor(Color(0.2f, 0.38f, 0.6f, 1f))
        val separador       = "- - - - - - - - - - - - - - - - - - - - - -"
        val medidaSeparador = GlyphLayout(juego.fuente, separador)
        juego.fuente.draw(juego.batch, separador, (anchoPantalla - medidaSeparador.width) / 2f, altoPantalla * 0.54f)

        juego.fuente.setColor(Color(0.92f, 0.88f, 0.42f, 1f))
        val opcion1       = "R / ENTER  ->  Volver a intentarlo"
        val medidaOpcion1 = GlyphLayout(juego.fuente, opcion1)
        juego.fuente.draw(juego.batch, opcion1, (anchoPantalla - medidaOpcion1.width) / 2f, altoPantalla * 0.48f)

        juego.fuente.setColor(Color(0.6f, 0.65f, 0.72f, 1f))
        val opcion2       = "M  ->  Menu principal"
        val medidaOpcion2 = GlyphLayout(juego.fuente, opcion2)
        juego.fuente.draw(juego.batch, opcion2, (anchoPantalla - medidaOpcion2.width) / 2f, altoPantalla * 0.42f)

        // texto parpadeante al fondo
        val brillo = abs(sin(System.currentTimeMillis() / 600.0)).toFloat()
        juego.fuente.setColor(Color(0.45f, 0.8f, 1f, 0.3f + brillo * 0.7f))
        val textoAyuda  = "Pulsa R para jugar o M para el menú"
        val medidaAyuda = GlyphLayout(juego.fuente, textoAyuda)

        juego.fuente.draw(juego.batch, textoAyuda, (anchoPantalla - medidaAyuda.width) / 2f, 30f)
        juego.batch.end()

        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
        {
            juego.setScreen(PlayScreen(juego)); dispose()
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
        {
            juego.setScreen(MenuScreen(juego)); dispose()
        }
    }

    override fun resize(ancho: Int, alto: Int)
    {
        viewportJuego.update(ancho, alto, true)
        viewportUI.update(ancho, alto, true)
    }
    override fun pause()  {}
    override fun resume() {}
    override fun hide()   {}

    override fun dispose()
    {
        textureFondo.dispose(); textureNave.dispose()
        texturaOverlay.dispose(); texturaPanel.dispose()
    }
}
