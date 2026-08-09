package io.github.mendietagarciaalejandro.ocrea.ui.favoritos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mendietagarciaalejandro.ocrea.R
import io.github.mendietagarciaalejandro.ocrea.ui.comun.AvisoVacio
import io.github.mendietagarciaalejandro.ocrea.ui.comun.TarjetaObra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFavoritos(
    onAbrirObra: (Int) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritosViewModel = hiltViewModel(),
) {
    val favoritos by viewModel.favoritos.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favoritos_titulo)) },
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
        if (favoritos.isEmpty()) {
            AvisoVacio(
                mensaje = stringResource(R.string.favoritos_vacio),
                modifier = Modifier.padding(relleno),
            )
            return@Scaffold
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .padding(relleno)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = favoritos, key = { it.id }) { obra ->
                TarjetaObra(obra = obra, onClick = { onAbrirObra(obra.id) })
            }
        }
    }
}
