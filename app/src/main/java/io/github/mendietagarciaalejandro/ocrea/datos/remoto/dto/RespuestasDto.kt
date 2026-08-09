package io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Respuesta de los endpoints que devuelven varias obras: listado y busqueda. */
@Serializable
data class RespuestaObrasDto(
    @SerialName("pagination")
    val paginacion: PaginacionDto,

    @SerialName("data")
    val obras: List<ObraDto>,
)

/**
 * Respuesta del detalle. La API devuelve la obra envuelta en "data" igual que en los
 * listados, solo que aqui es un objeto y no una lista.
 */
@Serializable
data class RespuestaObraDto(
    @SerialName("data")
    val obra: ObraDto,
)
