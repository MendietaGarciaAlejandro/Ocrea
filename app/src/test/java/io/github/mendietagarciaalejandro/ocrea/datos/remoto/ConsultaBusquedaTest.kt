package io.github.mendietagarciaalejandro.ocrea.datos.remoto

import io.github.mendietagarciaalejandro.ocrea.dominio.Departamento
import io.github.mendietagarciaalejandro.ocrea.dominio.FiltrosBusqueda
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConsultaBusquedaTest {

    @Test
    fun `sin filtros no se manda ningun parametro`() {
        val parametros = ConsultaBusqueda.parametros(FiltrosBusqueda(texto = "monet"))

        // El texto libre viaja aparte, en el parametro q.
        assertTrue(parametros.isEmpty())
    }

    @Test
    fun `el artista se manda como match`() {
        val parametros = ConsultaBusqueda.parametros(FiltrosBusqueda(artista = "  picasso "))

        assertEquals(
            mapOf("query[bool][must][0][match][artist_title]" to "picasso"),
            parametros,
        )
    }

    @Test
    fun `el departamento viaja con el nombre que entiende la api`() {
        val parametros = ConsultaBusqueda.parametros(
            FiltrosBusqueda(departamento = Departamento.ArteAsiatico),
        )

        assertEquals(
            mapOf("query[bool][must][0][match][department_title]" to "Arts of Asia"),
            parametros,
        )
    }

    @Test
    fun `el rango de anios usa gte y lte sobre date_start`() {
        val parametros = ConsultaBusqueda.parametros(FiltrosBusqueda(desde = 1900, hasta = 1950))

        assertEquals(
            mapOf(
                "query[bool][must][0][range][date_start][gte]" to "1900",
                "query[bool][must][1][range][date_start][lte]" to "1950",
            ),
            parametros,
        )
    }

    @Test
    fun `solo el limite inferior tambien vale`() {
        val parametros = ConsultaBusqueda.parametros(FiltrosBusqueda(desde = 1900))

        assertEquals(
            mapOf("query[bool][must][0][range][date_start][gte]" to "1900"),
            parametros,
        )
    }

    @Test
    fun `los filtros se numeran seguidos para que se sumen todos`() {
        val parametros = ConsultaBusqueda.parametros(
            FiltrosBusqueda(
                artista = "picasso",
                departamento = Departamento.ArteContemporaneo,
                desde = 1930,
                hasta = 1970,
            ),
        )

        // Si los indices no fueran correlativos, Elasticsearch descartaria condiciones.
        assertEquals(
            listOf(
                "query[bool][must][0][match][artist_title]",
                "query[bool][must][1][match][department_title]",
                "query[bool][must][2][range][date_start][gte]",
                "query[bool][must][3][range][date_start][lte]",
            ),
            parametros.keys.sorted(),
        )
    }

    @Test
    fun `los anios negativos se admiten porque hay piezas anteriores a Cristo`() {
        val parametros = ConsultaBusqueda.parametros(FiltrosBusqueda(desde = -2000, hasta = -500))

        assertEquals("-2000", parametros["query[bool][must][0][range][date_start][gte]"])
        assertEquals("-500", parametros["query[bool][must][1][range][date_start][lte]"])
    }
}
