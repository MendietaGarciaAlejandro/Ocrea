package io.github.mendietagarciaalejandro.ocrea.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.mendietagarciaalejandro.ocrea.ui.catalogo.PantallaCatalogo
import io.github.mendietagarciaalejandro.ocrea.ui.detalle.PantallaDetalle

@Composable
fun NavegacionOcrea(modifier: Modifier = Modifier) {
    val navegador = rememberNavController()

    NavHost(
        navController = navegador,
        startDestination = Catalogo,
        modifier = modifier,
    ) {
        composable<Catalogo> {
            PantallaCatalogo(onAbrirObra = { id -> navegador.navigate(Detalle(id)) })
        }

        composable<Detalle> {
            // El id lo lee el propio ViewModel desde SavedStateHandle.
            PantallaDetalle(onVolver = navegador::popBackStack)
        }
    }
}
