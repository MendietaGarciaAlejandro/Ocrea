package io.github.mendietagarciaalejandro.ocrea.ui.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.mendietagarciaalejandro.ocrea.R
import io.github.mendietagarciaalejandro.ocrea.dominio.ImagenesIiif
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import io.github.mendietagarciaalejandro.ocrea.ui.comun.AvisoError
import io.github.mendietagarciaalejandro.ocrea.ui.comun.AvisoVacio
import io.github.mendietagarciaalejandro.ocrea.ui.comun.Cargando
import io.github.mendietagarciaalejandro.ocrea.ui.comun.ImagenObra
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    onAbrirObra: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CatalogoViewModel = hiltViewModel(),
) {
    val obras = viewModel.obras.collectAsLazyPagingItems()
    val consulta by viewModel.consulta.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.catalogo_titulo)) }) },
    ) { relleno ->
        Column(modifier = Modifier.padding(relleno)) {
            CampoBusqueda(
                consulta = consulta,
                onEscribir = viewModel::alEscribir,
                onLimpiar = viewModel::limpiarBusqueda,
            )

            CuadriculaObras(
                obras = obras,
                consulta = consulta.trim(),
                onAbrirObra = onAbrirObra,
            )
        }
    }
}

@Composable
private fun CampoBusqueda(
    consulta: String,
    onEscribir: (String) -> Unit,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val teclado = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = consulta,
        onValueChange = onEscribir,
        placeholder = { Text(stringResource(R.string.buscar)) },
        singleLine = true,
        trailingIcon = {
            if (consulta.isNotEmpty()) {
                IconButton(onClick = onLimpiar) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.limpiar_busqueda),
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { teclado?.hide() }),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
    )
}

@Composable
private fun CuadriculaObras(
    obras: LazyPagingItems<Obra>,
    consulta: String,
    onAbrirObra: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cargaInicial = obras.loadState.refresh

    when {
        cargaInicial is LoadState.Loading && obras.itemCount == 0 -> Cargando(modifier)

        cargaInicial is LoadState.Error && obras.itemCount == 0 -> AvisoError(
            sinConexion = cargaInicial.error is IOException,
            onReintentar = obras::retry,
            modifier = modifier.fillMaxSize(),
        )

        cargaInicial is LoadState.NotLoading && obras.itemCount == 0 -> AvisoVacio(
            mensaje = if (consulta.length >= CatalogoViewModel.MINIMO_CARACTERES) {
                stringResource(R.string.busqueda_sin_resultados, consulta)
            } else {
                stringResource(R.string.catalogo_vacio)
            },
            modifier = modifier,
        )

        else -> LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(count = obras.itemCount) { indice ->
                val obra = obras[indice]
                if (obra != null) {
                    TarjetaObra(obra = obra, onClick = { onAbrirObra(obra.id) })
                }
            }

            // El error al seguir bajando no borra lo ya visto: se avisa al pie de la lista.
            val cargaSiguiente = obras.loadState.append
            if (cargaSiguiente is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Cargando()
                    }
                }
            }
            if (cargaSiguiente is LoadState.Error) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AvisoError(
                        sinConexion = cargaSiguiente.error is IOException,
                        onReintentar = obras::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaObra(
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
