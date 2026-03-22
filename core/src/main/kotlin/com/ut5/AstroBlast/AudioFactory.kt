package com.ut5.AstroBlast

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.files.FileHandle
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

object AudioFactory {

    fun sonidoCristal(): Sound {
        val frecuencia = 1200f // tono agudo
        val duracion   = 0.08f
        val muestras   = 22050
        val total      = (muestras * duracion).toInt()
        val datos      = ByteArray(total * 2)

        for (i in 0 until total) {
            val angulo  = 2.0 * Math.PI * frecuencia * i / muestras
            val volumen = if (i < total * 0.1) i / (total * 0.1)
                          else 1.0 - (i - total * 0.1) / (total * 0.9)
            val muestra = (Math.sin(angulo) * volumen * Short.MAX_VALUE * 0.55).toInt().toShort()
            datos[i * 2]     = (muestra.toInt() and 0xFF).toByte()
            datos[i * 2 + 1] = ((muestra.toInt() shr 8) and 0xFF).toByte()
        }

        val archivo = File.createTempFile("cristal", ".wav").also { it.deleteOnExit() }
        guardarWav(archivo, datos, muestras)
        return Gdx.audio.newSound(FileHandle(archivo))
    }

    fun sonidoImpacto(): Sound {
        val frecuencia = 90f // tono grave para el golpe
        val duracion   = 0.12f
        val muestras   = 22050
        val total      = (muestras * duracion).toInt()
        val datos      = ByteArray(total * 2)

        for (i in 0 until total) {
            val angulo  = 2.0 * Math.PI * frecuencia * i / muestras
            val volumen = if (i < total * 0.1) i / (total * 0.1)
                          else 1.0 - (i - total * 0.1) / (total * 0.9)
            val onda    = if (Math.sin(angulo) >= 0) 0.5 else -0.5
            val muestra = (onda * volumen * Short.MAX_VALUE * 0.55).toInt().toShort()
            datos[i * 2]     = (muestra.toInt() and 0xFF).toByte()
            datos[i * 2 + 1] = ((muestra.toInt() shr 8) and 0xFF).toByte()
        }

        val archivo = File.createTempFile("impacto", ".wav").also { it.deleteOnExit() }
        guardarWav(archivo, datos, muestras)
        return Gdx.audio.newSound(FileHandle(archivo))
    }

    // música de fondo: mezclo tres frecuencias para que suene a algo parecido al espacio
    fun musicaAmbiente(): Music {
        val muestras = 22050
        val total    = muestras * 6
        val datos    = ByteArray(total * 2)

        for (i in 0 until total) {
            val t     = i.toDouble() / muestras
            val valor = Math.sin(2.0 * Math.PI * 65.0  * t) * 0.3 +
                        Math.sin(2.0 * Math.PI * 130.0 * t) * 0.15 +
                        Math.sin(2.0 * Math.PI * 0.25  * t) * 0.2
            val muestra = (valor * Short.MAX_VALUE * 0.5).toInt().toShort()
            datos[i * 2]     = (muestra.toInt() and 0xFF).toByte()
            datos[i * 2 + 1] = ((muestra.toInt() shr 8) and 0xFF).toByte()
        }

        val archivo = File.createTempFile("ambiente", ".wav").also { it.deleteOnExit() }
        guardarWav(archivo, datos, muestras)
        return Gdx.audio.newMusic(FileHandle(archivo))
    }

    // guarda los datos en un archivo .wav temporal
    private fun guardarWav(archivo: File, datos: ByteArray, muestras: Int) {
        DataOutputStream(FileOutputStream(archivo)).use { s ->
            s.write("RIFF".toByteArray())
            s.write(byteArrayOf((36 + datos.size).toByte(), ((36 + datos.size) shr 8).toByte(), ((36 + datos.size) shr 16).toByte(), ((36 + datos.size) shr 24).toByte()))
            s.write("WAVEfmt ".toByteArray())
            s.write(byteArrayOf(16, 0, 0, 0, 1, 0, 1, 0))
            s.write(byteArrayOf((muestras).toByte(), (muestras shr 8).toByte(), (muestras shr 16).toByte(), (muestras shr 24).toByte()))
            s.write(byteArrayOf((muestras * 2).toByte(), ((muestras * 2) shr 8).toByte(), ((muestras * 2) shr 16).toByte(), ((muestras * 2) shr 24).toByte()))
            s.write(byteArrayOf(2, 0, 16, 0))
            s.write("data".toByteArray())
            s.write(byteArrayOf(datos.size.toByte(), (datos.size shr 8).toByte(), (datos.size shr 16).toByte(), (datos.size shr 24).toByte()))
            s.write(datos)
        }
    }
}
