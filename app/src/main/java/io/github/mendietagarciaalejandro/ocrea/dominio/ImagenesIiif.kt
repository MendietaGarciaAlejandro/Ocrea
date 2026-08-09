package io.github.mendietagarciaalejandro.ocrea.dominio

/**
 * El museo no devuelve URLs de imagen, solo un identificador. La imagen se pide a su
 * servidor IIIF indicando el tamaño que se quiere.
 *
 * Formato: {base}/{id}/{recorte}/{ancho},/{rotacion}/{calidad}.jpg
 */
object ImagenesIiif {

    private const val BASE = "https://www.artic.edu/iiif/2"

    /** Suficiente para una tarjeta de la cuadricula sin gastar datos de mas. */
    const val ANCHO_MINIATURA = 400

    /**
     * El museo recomienda 843 para el detalle: al pedir siempre el mismo ancho, sus
     * servidores sirven la imagen ya cacheada en vez de recortarla al vuelo.
     */
    const val ANCHO_DETALLE = 843

    fun url(imagenId: String, ancho: Int): String =
        "$BASE/$imagenId/full/$ancho,/0/default.jpg"
}
