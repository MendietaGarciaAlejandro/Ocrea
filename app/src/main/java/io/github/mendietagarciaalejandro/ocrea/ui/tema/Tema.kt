package io.github.mendietagarciaalejandro.ocrea.ui.tema

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = OcreOscuro,
    onPrimary = LienzoClaro,
    secondary = Sombra,
    onSecondary = LienzoClaro,
    tertiary = Ocre,
    background = LienzoClaro,
    onBackground = TintaClara,
    surface = LienzoClaro,
    onSurface = TintaClara,
    surfaceVariant = OcreClaro,
    onSurfaceVariant = Sombra,
)

private val EsquemaOscuro = darkColorScheme(
    primary = OcreClaro,
    onPrimary = TintaClara,
    secondary = SombraClara,
    onSecondary = TintaClara,
    tertiary = Ocre,
    background = LienzoOscuro,
    onBackground = TintaOscura,
    surface = LienzoOscuro,
    onSurface = TintaOscura,
    surfaceVariant = Sombra,
    onSurfaceVariant = OcreClaro,
)

/**
 * Tema de la app.
 *
 * No se usa color dinamico (Material You) a proposito: recolorea la interfaz a partir del
 * fondo de pantalla del movil y eso pelearia con los colores de los cuadros. Aqui interesa
 * lo contrario, que el marco sea neutro y el color lo ponga la obra.
 */
@Composable
fun TemaOcrea(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    contenido: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (temaOscuro) EsquemaOscuro else EsquemaClaro,
        typography = Tipografia,
        content = contenido,
    )
}
