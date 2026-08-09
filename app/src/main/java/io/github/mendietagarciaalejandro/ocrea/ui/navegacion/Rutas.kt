package io.github.mendietagarciaalejandro.ocrea.ui.navegacion

import kotlinx.serialization.Serializable

/**
 * Rutas tipadas: al ser objetos serializables, el compilador comprueba los argumentos
 * en vez de dejarlos en cadenas de texto con plantillas.
 */
@Serializable
data object Catalogo

@Serializable
data object Favoritos

@Serializable
data object Sobre

@Serializable
data class Detalle(val obraId: Int)

/**
 * El visor solo necesita la imagen y el título para el texto accesible, así que se le
 * pasan directamente en vez de volver a pedir la obra entera.
 */
@Serializable
data class Visor(val imagenId: String, val titulo: String)
