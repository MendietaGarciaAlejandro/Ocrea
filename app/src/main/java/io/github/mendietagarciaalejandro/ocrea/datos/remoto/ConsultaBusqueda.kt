package io.github.mendietagarciaalejandro.ocrea.datos.remoto

import io.github.mendietagarciaalejandro.ocrea.dominio.FiltrosBusqueda

/**
 * Traduce los filtros de la app a los parámetros que espera el buscador del museo, que por
 * dentro es un Elasticsearch.
 *
 * La forma es `query[bool][must][N][tipo][campo]=valor`. El "must" es lo que hace que los
 * filtros se sumen: probando el parámetro `q` junto a un rango de fechas, el texto solo
 * puntuaba los resultados y el total se disparaba, porque no llegaba a filtrar nada.
 */
object ConsultaBusqueda {

    fun parametros(filtros: FiltrosBusqueda): Map<String, String> = buildList {
        if (filtros.artista.isNotBlank()) {
            add(Condicion.Coincide(CAMPO_ARTISTA, filtros.artista.trim()))
        }

        filtros.departamento?.let { add(Condicion.Coincide(CAMPO_DEPARTAMENTO, it.nombreApi)) }

        // El rango se mira sobre date_start, que es el año en que se empezó la obra.
        filtros.desde?.let { add(Condicion.Rango(CAMPO_ANIO, "gte", it)) }
        filtros.hasta?.let { add(Condicion.Rango(CAMPO_ANIO, "lte", it)) }
    }.mapIndexed { indice, condicion -> condicion.aParametro(indice) }.toMap()

    private const val CAMPO_ARTISTA = "artist_title"
    private const val CAMPO_DEPARTAMENTO = "department_title"
    private const val CAMPO_ANIO = "date_start"

    private sealed interface Condicion {
        fun aParametro(indice: Int): Pair<String, String>

        data class Coincide(val campo: String, val valor: String) : Condicion {
            override fun aParametro(indice: Int) =
                "query[bool][must][$indice][match][$campo]" to valor
        }

        data class Rango(val campo: String, val operador: String, val valor: Int) : Condicion {
            override fun aParametro(indice: Int) =
                "query[bool][must][$indice][range][$campo][$operador]" to valor.toString()
        }
    }
}
