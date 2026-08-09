package io.github.mendietagarciaalejandro.ocrea.ui.visor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import io.github.mendietagarciaalejandro.ocrea.R
import io.github.mendietagarciaalejandro.ocrea.dominio.ImagenesIiif

private const val ESCALA_MINIMA = 1f
private const val ESCALA_MAXIMA = 6f

/** A cuánto salta el doble toque; suficiente para ver la pincelada sin perderse. */
private const val ESCALA_DOBLE_TOQUE = 2.5f

/**
 * Visor a pantalla completa con zoom.
 *
 * El fondo es negro y no sigue al tema: es lo que hacen los visores de imágenes porque
 * un marco oscuro no compite con los colores del cuadro. En una app de arte importa más
 * de lo normal.
 */
@Composable
fun PantallaVisor(
    imagenId: String,
    titulo: String,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var escala by remember { mutableFloatStateOf(ESCALA_MINIMA) }
    var desplazamiento by remember { mutableStateOf(Offset.Zero) }
    var tamanoCaja by remember { mutableStateOf(IntSize.Zero) }
    var cargando by remember { mutableStateOf(true) }

    /**
     * Impide arrastrar la imagen fuera de la pantalla. El límite se calcula sobre la caja
     * y no sobre la imagen dibujada, así que con obras muy apaisadas se puede desplazar un
     * poco de más por arriba y por abajo; a cambio no hace falta conocer sus proporciones.
     */
    fun dentroDeLimites(propuesto: Offset, conEscala: Float): Offset {
        val maximoX = (tamanoCaja.width * (conEscala - 1f)) / 2f
        val maximoY = (tamanoCaja.height * (conEscala - 1f)) / 2f

        return Offset(
            x = propuesto.x.coerceIn(-maximoX, maximoX),
            y = propuesto.y.coerceIn(-maximoY, maximoY),
        )
    }

    val gestos = rememberTransformableState { cambioEscala, arrastre, _ ->
        val nuevaEscala = (escala * cambioEscala).coerceIn(ESCALA_MINIMA, ESCALA_MAXIMA)

        escala = nuevaEscala
        desplazamiento = if (nuevaEscala == ESCALA_MINIMA) {
            // Al volver al tamaño original la imagen se recoloca sola: si no, quedaría
            // descentrada y sin forma de recuperarla salvo arrastrando a ciegas.
            Offset.Zero
        } else {
            dentroDeLimites(desplazamiento + arrastre, nuevaEscala)
        }
    }

    val escalaAnimada by animateFloatAsState(targetValue = escala, label = "escala")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onSizeChanged { tamanoCaja = it },
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = ImagenesIiif.url(imagenId, ImagenesIiif.ANCHO_VISOR),
            contentDescription = titulo,
            contentScale = ContentScale.Fit,
            onState = { estado -> cargando = estado is AsyncImagePainter.State.Loading },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { punto ->
                            if (escala > ESCALA_MINIMA) {
                                escala = ESCALA_MINIMA
                                desplazamiento = Offset.Zero
                            } else {
                                escala = ESCALA_DOBLE_TOQUE
                                // Se acerca hacia donde ha tocado el dedo, no al centro.
                                val centro = Offset(size.width / 2f, size.height / 2f)
                                desplazamiento = dentroDeLimites(
                                    (centro - punto) * (ESCALA_DOBLE_TOQUE - 1f),
                                    ESCALA_DOBLE_TOQUE,
                                )
                            }
                        },
                    )
                }
                .transformable(state = gestos)
                .graphicsLayer {
                    scaleX = escalaAnimada
                    scaleY = escalaAnimada
                    translationX = desplazamiento.x
                    translationY = desplazamiento.y
                },
        )

        if (cargando) {
            CircularProgressIndicator(color = Color.White)
        }

        IconButton(
            onClick = onCerrar,
            modifier = Modifier
                .align(Alignment.TopStart)
                .safeDrawingPadding()
                .padding(8.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.visor_cerrar),
                tint = Color.White,
            )
        }
    }
}
