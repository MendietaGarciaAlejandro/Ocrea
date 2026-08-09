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
