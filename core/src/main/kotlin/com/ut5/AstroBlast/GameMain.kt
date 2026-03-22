package com.ut5.AstroBlast

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

// Clase principal del juego
class GameMain : Game() {
    // lateinit significa que las inicializo luego en el create()
    lateinit var batch: SpriteBatch
    lateinit var fuente: BitmapFont

    override fun create()
    {
        batch  = SpriteBatch()
        fuente = BitmapFont()
        // Sin este filtro la fuente se ve pixelada cuando la dibujas
        fuente.region.texture.setFilter(
            Texture.TextureFilter.Linear,
            Texture.TextureFilter.Linear
        )
        setScreen(MenuScreen(this))
    }

    override fun dispose()
    {
        super.dispose()
        batch.dispose()
        fuente.dispose()
    }
}
