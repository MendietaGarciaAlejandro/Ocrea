package io.github.mendietagarciaalejandro.ocrea.dominio

/**
 * Una obra de la colección, ya en los términos de la app.
 *
 * Sigue habiendo campos nulos porque el museo de verdad no conoce el autor o la fecha de
 * muchas piezas: inventar un valor aquí sería mentir. Lo que se muestre en ese hueco lo
 * decide la interfaz, que es la que sabe de idiomas.
 */
data class Obra(
    val id: Int,
    val titulo: String,
    val artista: String?,
    val fecha: String?,
    val imagenId: String?,
    val tecnica: String? = null,
    val origen: String? = null,
    val dimensiones: String? = null,
    val descripcion: String? = null,
    val creditos: String? = null,
    val tipo: String? = null,
    val departamento: String? = null,
    val esDominioPublico: Boolean = false,
) {
    val tieneImagen: Boolean get() = imagenId != null

    /** Null cuando la obra no está digitalizada; la interfaz pinta un hueco en su lugar. */
    fun urlImagen(ancho: Int): String? = imagenId?.let { ImagenesIiif.url(it, ancho) }
}
