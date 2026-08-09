package io.github.mendietagarciaalejandro.ocrea.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.mendietagarciaalejandro.ocrea.ui.catalogo.PantallaCatalogo
import io.github.mendietagarciaalejandro.ocrea.ui.detalle.PantallaDetalle
import io.github.mendietagarciaalejandro.ocrea.ui.favoritos.PantallaFavoritos
import io.github.mendietagarciaalejandro.ocrea.ui.sobre.PantallaSobre
import io.github.mendietagarciaalejandro.ocrea.ui.visor.PantallaVisor

@Composable
fun NavegacionOcrea(modifier: Modifier = Modifier) {
    val navegador = rememberNavController()

    NavHost(
        navController = navegador,
        startDestination = Catalogo,
        modifier = modifier,
    ) {
        composable<Catalogo> {
            PantallaCatalogo(
                onAbrirObra = { id -> navegador.navigate(Detalle(id)) },
                onAbrirFavoritos = { navegador.navigate(Favoritos) },
                onAbrirSobre = { navegador.navigate(Sobre) },
            )
        }

        composable<Favoritos> {
            PantallaFavoritos(
                onAbrirObra = { id -> navegador.navigate(Detalle(id)) },
                onVolver = navegador::popBackStack,
            )
        }

        composable<Sobre> {
            PantallaSobre(onVolver = navegador::popBackStack)
        }

        composable<Detalle> {
            // El id lo lee el propio ViewModel desde SavedStateHandle.
            PantallaDetalle(
                onVolver = navegador::popBackStack,
                onAbrirImagen = { imagenId, titulo ->
                    navegador.navigate(Visor(imagenId, titulo))
                },
            )
        }

        composable<Visor> { entrada ->
            val visor = entrada.toRoute<Visor>()
            PantallaVisor(
                imagenId = visor.imagenId,
                titulo = visor.titulo,
                onCerrar = navegador::popBackStack,
            )
        }
    }
}
