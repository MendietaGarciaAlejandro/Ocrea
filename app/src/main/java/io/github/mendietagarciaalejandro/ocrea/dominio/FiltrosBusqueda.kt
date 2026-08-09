package io.github.mendietagarciaalejandro.ocrea.dominio

/**
 * Lo que el usuario ha pedido buscar. Todo es opcional y se combina: sin nada marcado
 * equivale a no buscar, y entonces se muestra el catálogo entero.
 */
data class FiltrosBusqueda(
    val texto: String = "",
    val artista: String = "",
    val departamento: Departamento? = null,
    val desde: Int? = null,
    val hasta: Int? = null,
) {
    val hayTexto: Boolean get() = texto.trim().length >= MINIMO_TEXTO

    val hayFiltros: Boolean
        get() = artista.isNotBlank() || departamento != null || desde != null || hasta != null

    val estaVacio: Boolean get() = !hayTexto && !hayFiltros

    /** Cuántos filtros (sin contar el texto) hay puestos, para el contador de la interfaz. */
    val cuantosFiltros: Int
        get() = listOfNotNull(
            artista.takeIf { it.isNotBlank() },
            departamento,
            desde,
            hasta,
        ).size

    fun sinFiltros(): FiltrosBusqueda = FiltrosBusqueda(texto = texto)

    companion object {
        /** Con una sola letra la búsqueda devuelve medio museo y no aporta nada. */
        const val MINIMO_TEXTO = 2

        /** El museo tiene piezas de casi cinco mil años atrás. */
        const val ANIO_MINIMO = -3000
        const val ANIO_MAXIMO = 2030
    }
}

/**
 * Departamentos del museo. Son fijos: en vez de pedir la lista a la API en cada arranque,
 * se dejan aquí porque cambian cada muchos años y así los filtros funcionan sin conexión.
 */
enum class Departamento(val nombreApi: String) {
    PinturaEsculturaEuropa("Painting and Sculpture of Europe"),
    ArteContemporaneo("Contemporary Art"),
    ArteAmericano("Arts of the Americas"),
    ArteAsiatico("Arts of Asia"),
    ArteAfricano("Arts of Africa"),
    GrabadosDibujos("Prints and Drawings"),
    Fotografia("Photography and Media"),
    ArteTextil("Textiles"),
    ArteAplicado("Applied Arts of Europe"),
    Arquitectura("Architecture and Design"),
}
