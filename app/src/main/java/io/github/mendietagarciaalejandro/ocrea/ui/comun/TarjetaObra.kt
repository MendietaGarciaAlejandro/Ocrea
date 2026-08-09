package io.github.mendietagarciaalejandro.ocrea.ui.comun

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mendietagarciaalejandro.ocrea.R
import io.github.mendietagarciaalejandro.ocrea.dominio.ImagenesIiif
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra

/** La misma tarjeta sirve para el catálogo, la búsqueda y los favoritos. */
@Composable
fun TarjetaObra(
    obra: Obra,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
    ) {
        ImagenObra(
            url = obra.urlImagen(ImagenesIiif.ANCHO_MINIATURA),
            descripcion = obra.titulo,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = obra.titulo,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = obra.artista ?: stringResource(R.string.autor_desconocido),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        obra.fecha?.let { fecha ->
            Text(
                text = fecha,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}
