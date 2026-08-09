package io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Bloque de paginacion que acompaña a cualquier listado de la API. */
@Serializable
data class PaginacionDto(
    val total: Int,

    @SerialName("limit")
    val limite: Int,

    @SerialName("current_page")
    val paginaActual: Int,

    @SerialName("total_pages")
    val totalPaginas: Int,
)
