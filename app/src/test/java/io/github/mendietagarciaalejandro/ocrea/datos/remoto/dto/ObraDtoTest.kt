package io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los ficheros de /resources son respuestas reales de la API, no inventadas.
 * Si el museo cambia el contrato, estos tests son los que se enteran.
 */
class ObraDtoTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private fun leerRecurso(nombre: String): String =
        checkNotNull(javaClass.getResource("/$nombre")) { "falta el fichero $nombre" }.readText()

    @Test
    fun `el listado se deserializa con su paginacion`() {
        val respuesta = json.decodeFromString<RespuestaObrasDto>(leerRecurso("listado.json"))

        assertEquals(3, respuesta.paginacion.limite)
        assertEquals(1, respuesta.paginacion.paginaActual)
        assertTrue(respuesta.paginacion.total > 100_000)
        assertEquals(3, respuesta.obras.size)
    }

    @Test
    fun `las obras sin artista conocido no rompen la deserializacion`() {
        val respuesta = json.decodeFromString<RespuestaObrasDto>(leerRecurso("listado.json"))

        // En la coleccion hay muchas piezas sin autor atribuido: el campo llega ausente.
        val sinArtista = respuesta.obras.count { it.artista == null }

        assertTrue("se esperaba alguna obra sin artista en la muestra", sinArtista > 0)
    }

    @Test
    fun `las claves que no se piden se ignoran`() {
        // La respuesta trae next_url, info y config, que no estan en los DTO.
        val respuesta = json.decodeFromString<RespuestaObrasDto>(leerRecurso("listado.json"))

        assertEquals(3, respuesta.obras.size)
    }

    @Test
    fun `el detalle viene envuelto en data`() {
        val respuesta = json.decodeFromString<RespuestaObraDto>(leerRecurso("detalle.json"))
        val obra = respuesta.obra

        assertEquals(27992, obra.id)
        assertEquals("A Sunday on La Grande Jatte — 1884", obra.titulo)
        assertNotNull(obra.imagenId)
        assertNotNull(obra.tecnica)
    }

    @Test
    fun `la descripcion del museo llega en html`() {
        val obra = json.decodeFromString<RespuestaObraDto>(leerRecurso("detalle.json")).obra

        val descripcion = checkNotNull(obra.descripcion)
        assertTrue("se esperaba marcado html", descripcion.contains("<p>"))
    }

    @Test
    fun `una obra minima solo necesita id y titulo`() {
        val minima = """{"id": 1, "title": "Sin mas datos"}"""

        val obra = json.decodeFromString<ObraDto>(minima)

        assertEquals(1, obra.id)
        assertNull(obra.imagenId)
        assertNull(obra.artista)
        assertEquals(false, obra.esDominioPublico)
    }
}
