# AstroBlast

## Introducción

AstroBlast es un juego 2D en Kotlin con libGDX. La nave cruza el cinturón de asteroides y tiene que sobrevivir 90 segundos esquivando meteoritos y recogiendo cristales para mantener el escudo.

- **Nave**: se mueve horizontalmente en la parte inferior.
- **Meteorito pequeño**: quita 20 de escudo.
- **Meteorito grande**: quita 35 de escudo, aparece a los 30s.
- **Cristal**: recupera 20 de escudo.

## Desarrollo

**Colisiones:** cada objeto tiene un `Rectangle`. 
**Delta time:** todo el movimiento multiplica por `delta` para que la velocidad sea igual en cualquier equipo.

**Spawn:** los objetos aparecen en una X aleatoria arriba de la pantalla. El intervalo entre spawns va bajando con el tiempo.

**Escudo:** variable `float` de 0 a 100. A 0 es derrota, aguantar 90s es victoria.

**Clases:**
- `GameMain` — clase principal, gestiona batch, fuente y pantallas.
- `MenuScreen` — menú con instrucciones.
- `PlayScreen` — lógica del juego: input, colisiones, spawn, HUD.
- `GameOverScreen` — resultado con opción de reinicio.
- `EntityType` — enum con `ROCK`, `BIGROCK`, `CRYSTAL`.
- `TextureFactory` — genera los gráficos con `Pixmap`
- `AudioFactory` — genera los sonidos en WAV por código

