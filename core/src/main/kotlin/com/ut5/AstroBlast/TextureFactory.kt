package com.ut5.AstroBlast

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import java.util.Random

// Dibujo los gráficos con Pixmap
object TextureFactory
{
    fun makeColorSolido(ancho: Int, alto: Int, color: Color): Texture
    {
        val lienzo = Pixmap(ancho, alto, Pixmap.Format.RGBA8888)
        lienzo.setColor(color)
        lienzo.fill()
        return Texture(lienzo).also { lienzo.dispose() }
    }

    fun makeFondo(ancho: Int, alto: Int): Texture
    {
        val lienzo = Pixmap(ancho, alto, Pixmap.Format.RGBA8888)

        for (y in 0 until alto) {
            val t = y.toFloat() / alto
            lienzo.setColor(0f, t * 0.03f, 0.04f + t * 0.08f, 1f)
            lienzo.drawLine(0, y, ancho, y)
        }

        val rng = Random(42L)
        repeat(300) {
            val nx = (ancho * 0.55f + rng.nextGaussian() * ancho * 0.18f).toInt().coerceIn(0, ancho - 1)
            val ny = (alto  * 0.25f + rng.nextGaussian() * alto  * 0.12f).toInt().coerceIn(0, alto  - 1)
            lienzo.setColor(0.15f, 0.1f, 0.5f, rng.nextFloat() * 0.06f)
            lienzo.fillCircle(nx, ny, rng.nextInt(4) + 1)
        }

        val rng2 = Random(7L)
        lienzo.setColor(1f, 1f, 1f, 0.9f)
        repeat(120) { lienzo.drawPixel(rng2.nextInt(ancho), rng2.nextInt(alto)) }

        repeat(30) {
            lienzo.setColor(0.8f + rng2.nextFloat() * 0.2f, 0.85f + rng2.nextFloat() * 0.15f, 1f, 1f)
            lienzo.fillCircle(rng2.nextInt(ancho), rng2.nextInt(alto), 2)
        }

        repeat(8) {
            val ex = rng2.nextInt(ancho)
            val ey = rng2.nextInt(alto)
            lienzo.setColor(1f, 1f, 1f, 1f)
            lienzo.fillCircle(ex, ey, 3)
            lienzo.setColor(1f, 1f, 1f, 0.4f)
            for (d in 1..5) {
                if (ex + d < ancho) lienzo.drawPixel(ex + d, ey)
                if (ex - d >= 0)    lienzo.drawPixel(ex - d, ey)
                if (ey + d < alto)  lienzo.drawPixel(ex, ey + d)
                if (ey - d >= 0)    lienzo.drawPixel(ex, ey - d)
            }
        }

        return Texture(lienzo).also { lienzo.dispose() }
    }

    fun makeNave(ancho: Int, alto: Int): Texture
    {
        val lienzo  = Pixmap(ancho, alto, Pixmap.Format.RGBA8888)
        lienzo.setColor(0f, 0f, 0f, 0f)
        lienzo.fill()
        val cx = ancho / 2

        lienzo.setColor(0.35f, 0.38f, 0.45f, 1f)
        lienzo.fillTriangle(cx, alto * 2 / 5, cx - ancho / 2, alto * 4 / 5, cx - ancho / 6, alto * 4 / 5)
        lienzo.fillTriangle(cx, alto * 2 / 5, cx + ancho / 2, alto * 4 / 5, cx + ancho / 6, alto * 4 / 5)

        lienzo.setColor(0.72f, 0.76f, 0.82f, 1f)
        for (y in alto / 10 until alto * 9 / 10) {
            val t  = (y - alto / 10).toFloat() / (alto * 8 / 10)
            val hw = when {
                t < 0.35f -> (t / 0.35f) * (ancho / 6f)
                t < 0.65f -> ancho / 6f
                else      -> ((1f - t) / 0.35f) * (ancho / 6f)
            }
            if (hw >= 1f) lienzo.drawLine((cx - hw).toInt(), y, (cx + hw).toInt(), y)
        }

        lienzo.setColor(0.55f, 0.58f, 0.65f, 1f)
        for (y in alto / 10 until alto * 8 / 10) lienzo.drawPixel(cx, y)

        lienzo.setColor(0.28f, 0.30f, 0.38f, 1f)
        for (i in 0 until 8) {
            val y = alto * 3 / 5 + i
            if (cx - ancho / 5 - i >= 0 && y < alto) lienzo.drawPixel(cx - ancho / 5 - i, y)
            if (cx + ancho / 5 + i < ancho && y < alto) lienzo.drawPixel(cx + ancho / 5 + i, y)
        }

        lienzo.setColor(0.3f, 0.65f, 1f, 0.95f)
        lienzo.fillCircle(cx, alto * 3 / 10, ancho / 10)
        lienzo.setColor(0.8f, 0.9f, 1f, 0.7f)
        lienzo.fillCircle(cx - 2, alto * 3 / 10 - 2, ancho / 22)

        lienzo.setColor(0.25f, 0.27f, 0.32f, 1f)
        lienzo.fillRectangle(cx - ancho / 10, alto * 8 / 10, ancho / 5, alto / 10)
        lienzo.setColor(1f, 0.55f, 0.1f, 1f)
        lienzo.fillTriangle(cx - ancho / 10, alto * 9 / 10, cx + ancho / 10, alto * 9 / 10, cx, alto - 1)
        lienzo.setColor(1f, 0.9f, 0.4f, 1f)
        lienzo.fillTriangle(cx - ancho / 18, alto * 9 / 10, cx + ancho / 18, alto * 9 / 10, cx, alto * 19 / 20)

        return Texture(lienzo).also { lienzo.dispose() }
    }

    fun makePiedra(ancho: Int, alto: Int): Texture
    {
        val lienzo = Pixmap(ancho, alto, Pixmap.Format.RGBA8888)
        lienzo.setColor(0f, 0f, 0f, 0f)
        lienzo.fill()
        val cx = ancho / 2
        val cy = alto / 2

        lienzo.setColor(0.48f, 0.43f, 0.38f, 1f); lienzo.fillCircle(cx, cy, ancho / 2 - 1)
        lienzo.setColor(0.32f, 0.28f, 0.24f, 1f); lienzo.fillCircle(cx + ancho / 5, cy + alto / 5, ancho / 4)
        lienzo.setColor(0.62f, 0.57f, 0.50f, 1f); lienzo.fillCircle(cx - ancho / 5, cy - alto / 5, ancho / 5)
        lienzo.setColor(0.22f, 0.19f, 0.16f, 1f); lienzo.fillCircle(cx - ancho / 7, cy + alto / 8, ancho / 7)
        lienzo.setColor(0.55f, 0.50f, 0.44f, 1f); lienzo.drawCircle(cx - ancho / 7, cy + alto / 8, ancho / 7)
        lienzo.setColor(0.22f, 0.19f, 0.16f, 1f); lienzo.fillCircle(cx + ancho / 5, cy - alto / 6, ancho / 10)
        lienzo.setColor(0.20f, 0.18f, 0.15f, 0.8f)
        lienzo.drawLine(cx, cy - alto / 4, cx + ancho / 4, cy + alto / 4)
        lienzo.drawLine(cx - ancho / 5, cy, cx + ancho / 6, cy + alto / 5)

        return Texture(lienzo).also { lienzo.dispose() }
    }

    fun makePiedraGrande(ancho: Int, alto: Int): Texture
    {
        val lienzo = Pixmap(ancho, alto, Pixmap.Format.RGBA8888)
        lienzo.setColor(0f, 0f, 0f, 0f)
        lienzo.fill()
        val cx = ancho / 2
        val cy = alto / 2

        lienzo.setColor(0.38f, 0.30f, 0.25f, 1f); lienzo.fillCircle(cx, cy, ancho / 2 - 1)
        lienzo.setColor(0.22f, 0.16f, 0.12f, 1f); lienzo.fillCircle(cx + ancho / 4, cy + alto / 4, ancho / 3)
        lienzo.setColor(0.52f, 0.42f, 0.35f, 1f); lienzo.fillCircle(cx - ancho / 6, cy - alto / 6, ancho / 6)
        lienzo.setColor(0.15f, 0.10f, 0.08f, 1f); lienzo.fillCircle(cx - ancho / 5, cy + alto / 6, ancho / 6)
        lienzo.setColor(0.42f, 0.34f, 0.28f, 1f); lienzo.drawCircle(cx - ancho / 5, cy + alto / 6, ancho / 6)
        lienzo.setColor(0.15f, 0.10f, 0.08f, 1f); lienzo.fillCircle(cx + ancho / 6, cy - alto / 5, ancho / 8)
        lienzo.setColor(0.42f, 0.34f, 0.28f, 1f); lienzo.drawCircle(cx + ancho / 6, cy - alto / 5, ancho / 8)
        lienzo.setColor(0.12f, 0.09f, 0.07f, 1f)
        lienzo.drawLine(cx - ancho / 3, cy - alto / 3, cx + ancho / 3, cy + alto / 4)
        lienzo.drawLine(cx + ancho / 4, cy - alto / 3, cx - ancho / 4, cy + alto / 3)

        for (angulo in 0 until 360 step 10)
        {
            val rad = Math.toRadians(angulo.toDouble())
            val bx  = (cx + (ancho / 2 - 2) * Math.cos(rad)).toInt()
            val by  = (cy + (alto  / 2 - 2) * Math.sin(rad)).toInt()
            if (bx in 0 until ancho && by in 0 until alto) {
                lienzo.setColor(0.7f, 0.2f, 0.05f, 0.6f)
                lienzo.drawPixel(bx, by)
            }
        }

        return Texture(lienzo).also { lienzo.dispose() }
    }

    fun makeCristal(ancho: Int, alto: Int): Texture
    {
        val lienzo = Pixmap(ancho, alto, Pixmap.Format.RGBA8888)
        lienzo.setColor(0f, 0f, 0f, 0f)
        lienzo.fill()
        val cx = ancho / 2
        val cy = alto / 2

        for (r in ancho / 2 downTo ancho / 3)
        {
            val alfa = (1f - (r - ancho / 3f) / (ancho / 6f).coerceAtLeast(1f)) * 0.18f
            lienzo.setColor(0.1f, 0.9f, 0.55f, alfa.coerceIn(0f, 0.18f))
            lienzo.fillCircle(cx, cy, r)
        }

        val vertices = Array(6) { i ->
            val a = Math.toRadians(i * 60.0 - 30.0)
            Pair(cx + (ancho / 3.2f * Math.cos(a)).toInt(), cy + (alto / 3.2f * Math.sin(a)).toInt())
        }

        lienzo.setColor(0.05f, 0.75f, 0.50f, 1f)
        for (i in vertices.indices)
        {
            val sig = vertices[(i + 1) % vertices.size]
            lienzo.fillTriangle(cx, cy, vertices[i].first, vertices[i].second, sig.first, sig.second)
        }

        lienzo.setColor(0.25f, 1f, 0.72f, 0.9f)
        lienzo.fillTriangle(cx, cy, vertices[0].first, vertices[0].second, vertices[1].first, vertices[1].second)
        lienzo.fillTriangle(cx, cy, vertices[1].first, vertices[1].second, vertices[2].first, vertices[2].second)

        lienzo.setColor(0.8f, 1f, 0.9f, 0.95f); lienzo.fillCircle(cx, cy, ancho / 8)
        lienzo.setColor(1f, 1f, 1f, 0.85f)
        lienzo.drawLine(cx - ancho / 7, cy - alto / 6, cx + ancho / 10, cy - alto / 10)

        lienzo.setColor(0.02f, 0.45f, 0.30f, 0.9f)
        for (i in vertices.indices)
        {
            val sig = vertices[(i + 1) % vertices.size]
            lienzo.drawLine(vertices[i].first, vertices[i].second, sig.first, sig.second)
            lienzo.drawLine(cx, cy, vertices[i].first, vertices[i].second)
        }

        return Texture(lienzo).also { lienzo.dispose() }
    }
}
