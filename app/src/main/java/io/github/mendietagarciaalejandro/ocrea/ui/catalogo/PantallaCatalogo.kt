package io.github.mendietagarciaalejandro.ocrea.ui.catalogo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import io.github.mendietagarciaalejandro.ocrea.R
import io.github.mendietagarciaalejandro.ocrea.dominio.FiltrosBusqueda
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import io.github.mendietagarciaalejandro.ocrea.ui.comun.AvisoError
import io.github.mendietagarciaalejandro.ocrea.ui.comun.AvisoVacio
import io.github.mendietagarciaalejandro.ocrea.ui.comun.Cargando
import io.github.mendietagarciaalejandro.ocrea.ui.comun.TarjetaObra
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    onAbrirObra: (Int) -> Unit,
    onAbrirFavoritos: () -> Unit,
    onAbrirSobre: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CatalogoViewModel = hiltViewModel(),
) {
    val obras = viewModel.obras.collectAsLazyPagingItems()
    val filtros by viewModel.filtros.collectAsStateWithLifecycle()
    var panelAbierto by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.catalogo_titulo)) },
                actions = {
                    IconButton(onClick = { panelAbierto = !panelAbierto }) {
                        BadgedBox(
                            badge = {
                                if (filtros.cuantosFiltros > 0) {
                                    Badge { Text(filtros.cuantosFiltros.toString()) }
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_filtros),
                                contentDescription = stringResource(R.string.filtros),
                            )
                        }
                    }
                    IconButton(onClick = onAbrirFavoritos) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = stringResource(R.string.abrir_favoritos),
                        )
                    }
                    IconButton(onClick = onAbrirSobre) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.abrir_sobre),
                        )
                    }
                },
            )
        },
    ) { relleno ->
        Column(modifier = Modifier.padding(relleno)) {
            CampoBusqueda(
                consulta = filtros.texto,
                onEscribir = viewModel::alEscribir,
                onLimpiar = viewModel::limpiarTexto,
            )

            AnimatedVisibility(visible = panelAbierto) {
                PanelFiltros(
                    filtros = filtros,
                    onArtista = viewModel::alCambiarArtista,
                    onDepartamento = viewModel::alCambiarDepartamento,
                    onAnios = viewModel::alCambiarAnios,
                    onLimpiar = viewModel::limpiarFiltros,
                )
            }

            CuadriculaObras(
                obras = obras,
                filtros = filtros,
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
    filtros: FiltrosBusqueda,
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
            mensaje = when {
                filtros.hayTexto -> stringResource(
                    R.string.busqueda_sin_resultados,
                    filtros.texto.trim(),
                )

                filtros.hayFiltros -> stringResource(R.string.busqueda_sin_resultados_filtros)
                else -> stringResource(R.string.catalogo_vacio)
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
