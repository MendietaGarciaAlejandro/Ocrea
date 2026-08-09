package io.github.mendietagarciaalejandro.ocrea.ui.detalle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mendietagarciaalejandro.ocrea.R
import io.github.mendietagarciaalejandro.ocrea.dominio.ImagenesIiif
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import io.github.mendietagarciaalejandro.ocrea.ui.comun.AvisoError
import io.github.mendietagarciaalejandro.ocrea.ui.comun.Cargando
import io.github.mendietagarciaalejandro.ocrea.ui.comun.ImagenObra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetalleViewModel = hiltViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    val titulo = when (val actual = estado) {
        is EstadoDetalle.Contenido -> actual.obra.titulo
        else -> stringResource(R.string.detalle_titulo)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = titulo, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.volver),
                        )
                    }
                },
            )
        },
    ) { relleno ->
        when (val actual = estado) {
            EstadoDetalle.Cargando -> Cargando(Modifier.padding(relleno))

            is EstadoDetalle.Error -> AvisoError(
                sinConexion = actual.sinConexion,
                onReintentar = viewModel::cargar,
                modifier = Modifier.padding(relleno).fillMaxSize(),
            )

            is EstadoDetalle.Contenido -> ContenidoObra(
                obra = actual.obra,
                modifier = Modifier.padding(relleno),
            )
        }
    }
}

@Composable
private fun ContenidoObra(obra: Obra, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ImagenObra(
            url = obra.urlImagen(ImagenesIiif.ANCHO_DETALLE),
            descripcion = obra.titulo,
            escala = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth(),
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = obra.titulo, style = MaterialTheme.typography.titleLarge)
            Text(
                text = obra.artista ?: stringResource(R.string.autor_desconocido),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            obra.descripcion?.let { descripcion ->
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp))

            Text(
                text = stringResource(R.string.ficha_tecnica),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            FilaFicha(R.string.ficha_fecha, obra.fecha)
            FilaFicha(R.string.ficha_tecnica_material, obra.tecnica)
            FilaFicha(R.string.ficha_origen, obra.origen)
            FilaFicha(R.string.ficha_dimensiones, obra.dimensiones)
            FilaFicha(R.string.ficha_tipo, obra.tipo)
            FilaFicha(R.string.ficha_departamento, obra.departamento)
            FilaFicha(R.string.ficha_creditos, obra.creditos)

            if (obra.esDominioPublico) {
                Text(
                    text = stringResource(R.string.dominio_publico),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp),
                )
            }
        }
    }
}

/** Los campos que el museo no tiene no se pintan: mejor un hueco que un "desconocido". */
@Composable
private fun FilaFicha(etiqueta: Int, valor: String?) {
    if (valor == null) return

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = stringResource(etiqueta),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.35f),
        )
        Text(text = valor, style = MaterialTheme.typography.bodyMedium)
    }
}
