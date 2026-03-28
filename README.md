<div align="center">

<h1>AstroBlast</h1>

<p>
  <img src="https://img.shields.io/badge/Platform-Desktop-blue?style=flat-square&logo=windows&logoColor=white"/>
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Framework-libGDX-E74C3C?style=flat-square"/>
  <img src="https://img.shields.io/badge/Build-Gradle-02303A?style=flat-square&logo=gradle&logoColor=white"/>
  <img src="https://img.shields.io/badge/Status-Completado-2EA44F?style=flat-square"/>
</p>

</div>

<hr/>

## ¿Qué es AstroBlast?

Juego 2D arcade desarrollado en Kotlin con el framework libGDX. Tu nave cruza el cinturón de asteroides y tiene que sobrevivir **90 segundos** esquivando meteoritos y recogiendo cristales de energía para mantener el escudo por encima de cero.

> Basado en el tutorial *A Simple Game (Drop)* de libGDX, extendido con mecánicas propias.

---

## Controles

<div align="center">

| Tecla | Acción |
|---|---|
| `A` / `D` o flechas | Mover la nave |
| Click / toque | Mover al punto tocado |
| `E` | Activar imán *(3s, recarga 8s)* |

</div>

---

## Objetos

<div align="center">

| Objeto | Efecto |
|---|---|
| 🪨 Meteorito pequeño | -20 escudo |
| 🔴 Meteorito grande | -35 escudo *(aparece a los 30s)* |
| 💎 Cristal verde | +20 escudo |

</div>

---

## Mecánicas

**Escudo**
Variable continua de 0 a 100. Si llega a 0 pierdes. Si sobrevives 90 segundos, ganas.

**Dificultad progresiva**
La velocidad de caída y el ritmo de aparición de objetos aumentan con el tiempo. A partir del segundo 30 empiezan a aparecer los meteoritos rojos.

<details>
<summary><b>💎 Sistema de colisiones (AABB)</b></summary>
Cada objeto tiene un rectángulo invisible asociado. Cada frame se comprueba con <code>rectNave.overlaps(rectObjeto)</code> y si hay colisión se aplica el efecto correspondiente.
</details>

<details>
<summary><b>🧲 Power-up: Imán (E)</b></summary>
Al pulsar E, todos los cristales en pantalla calculan el vector dirección hacia la nave, lo normalizan y se mueven a velocidad constante hacia ella durante 3 segundos. La nave se pone verde mientras está activo. Cooldown de 8 segundos.
</details>

<details>
<summary><b>⏱ Delta Time</b></summary>
Todo el movimiento multiplica por <code>delta</code> para que la velocidad sea exactamente igual independientemente de los FPS del equipo.
</details>

---

## Estructura del proyecto
```
core/
  └── com/ut5/AstroBlast/
        ├── GameMain.kt          clase principal, gestiona batch y pantallas
        ├── MenuScreen.kt        pantalla de menú con instrucciones
        ├── PlayScreen.kt        lógica completa del juego
        ├── GameOverScreen.kt    pantalla de victoria o derrota
        ├── EntityType.kt        enum con ROCK, BIGROCK y CRYSTAL
        ├── TextureFactory.kt    todos los gráficos generados con Pixmap
        └── AudioFactory.kt      sonidos generados por código en WAV/PCM
```

> Todos los gráficos y sonidos se generan por código, sin ningún archivo externo en assets.

---

## Pantallas

**Menú** → explica los controles y los objetos antes de empezar

**Juego** → nave + objetos cayendo + HUD con escudo, tiempo y cristales

**Resultado** → muestra si ganaste o perdiste y los cristales recogidos, con opción de reiniciar o volver al menú

---

## 👤 Autor
- Moree7
