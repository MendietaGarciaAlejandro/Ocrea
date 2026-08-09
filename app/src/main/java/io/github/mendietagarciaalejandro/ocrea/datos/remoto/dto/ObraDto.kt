package io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Una obra tal y como la devuelve la API del Art Institute of Chicago.
 *
 * Casi todo es nulable a proposito: la coleccion tiene piezas sin digitalizar, sin autor
 * conocido o sin catalogar. Solo el id y el titulo vienen siempre.
 */
@Serializable
data class ObraDto(
    val id: Int,

    @SerialName("title")
    val titulo: String,

    @SerialName("artist_title")
    val artista: String? = null,

    /** Atribucion completa: puede incluir varios autores, nacionalidad y fechas. */
    @SerialName("artist_display")
    val atribucion: String? = null,

    @SerialName("date_display")
    val fecha: String? = null,

    /** Con esto se construye la URL IIIF de la imagen. Null si la obra no tiene foto. */
    @SerialName("image_id")
    val imagenId: String? = null,

    @SerialName("medium_display")
    val tecnica: String? = null,

    @SerialName("place_of_origin")
    val origen: String? = null,

    @SerialName("dimensions")
    val dimensiones: String? = null,

    /** Texto del museo. Llega en HTML, no en texto plano. */
    @SerialName("description")
    val descripcion: String? = null,

    @SerialName("credit_line")
    val creditos: String? = null,

    @SerialName("artwork_type_title")
    val tipo: String? = null,

    @SerialName("department_title")
    val departamento: String? = null,

    @SerialName("is_public_domain")
    val esDominioPublico: Boolean = false,
)
