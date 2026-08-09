package io.github.mendietagarciaalejandro.ocrea.ui.comun

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.mendietagarciaalejandro.ocrea.R

/**
 * Buena parte de la colección no está digitalizada, así que la falta de imagen es un caso
 * normal y no un error: se pinta un hueco con el mismo aspecto que el resto de tarjetas.
 */
@Composable
fun ImagenObra(
    url: String?,
    descripcion: String,
    modifier: Modifier = Modifier,
    escala: ContentScale = ContentScale.Crop,
) {
    if (url == null) {
        SinImagen(modifier = modifier)
        return
    }

    AsyncImage(
        model = url,
        contentDescription = descripcion,
        contentScale = escala,
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Composable
private fun SinImagen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.sin_imagen),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp),
        )
    }
}
