package io.github.mendietagarciaalejandro.ocrea.datos

import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.ObraDto
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.RespuestaObraDto
import io.github.mendietagarciaalejandro.ocrea.dominio.ImagenesIiif
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapeadorObraTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private fun leerRecurso(nombre: String): String =
        checkNotNull(javaClass.getResource("/$nombre")) { "falta el fichero $nombre" }.readText()

    @Test
    fun `una obra sin imagen no tiene url`() {
        val obra = ObraDto(id = 1, titulo = "Sin digitalizar").aDominio()

        assertFalse(obra.tieneImagen)
        assertNull(obra.urlImagen(ImagenesIiif.ANCHO_MINIATURA))
    }

    @Test
    fun `la url de imagen se monta con el ancho pedido`() {
        val obra = ObraDto(id = 1, titulo = "Con foto", imagenId = "abc-123").aDominio()

        assertEquals(
            "https://www.artic.edu/iiif/2/abc-123/full/400,/0/default.jpg",
            obra.urlImagen(400),
        )
        assertEquals(
            "https://www.artic.edu/iiif/2/abc-123/full/843,/0/default.jpg",
            obra.urlImagen(ImagenesIiif.ANCHO_DETALLE),
        )
    }

    @Test
    fun `las cadenas vacias se convierten en nulo`() {
        val obra = ObraDto(id = 1, titulo = "Titulo", artista = "   ", fecha = "").aDominio()

        assertNull(obra.artista)
        assertNull(obra.fecha)
    }

    @Test
    fun `el html de la descripcion se convierte en texto plano`() {
        val dto = ObraDto(
            id = 1,
            titulo = "Prueba",
            descripcion = "<p>Primer <em>parrafo</em>.</p><p>Segundo &amp; ultimo.</p>",
        )

        val descripcion = checkNotNull(dto.aDominio().descripcion)

        assertFalse("no deberia quedar marcado", descripcion.contains("<"))
        assertTrue(descripcion.startsWith("Primer parrafo."))
        assertTrue(descripcion.endsWith("Segundo & ultimo."))
        assertTrue("los parrafos se separan", descripcion.contains("\n\n"))
    }

    @Test
    fun `las entidades html se decodifican`() {
        val dto = ObraDto(
            id = 1,
            titulo = "Prueba",
            descripcion = "Ferris&rsquo;s day off &mdash; &quot;Cameron&quot; &amp; Sloane",
        )

        assertEquals(
            "Ferris’s day off — \"Cameron\" & Sloane",
            dto.aDominio().descripcion,
        )
    }

    @Test
    fun `el detalle real del museo queda legible`() {
        val dto = json.decodeFromString<RespuestaObraDto>(leerRecurso("detalle.json")).obra

        val obra = dto.aDominio()
        val descripcion = checkNotNull(obra.descripcion)

        assertFalse(descripcion.contains("<p>"))
        assertFalse(descripcion.contains("&"))
        assertTrue(descripcion.isNotBlank())
        assertEquals("A Sunday on La Grande Jatte — 1884", obra.titulo)
        assertTrue(obra.tieneImagen)
    }

    @Test
    fun `mapear una lista mantiene el orden`() {
        val dtos = listOf(
            ObraDto(id = 3, titulo = "Tercera"),
            ObraDto(id = 1, titulo = "Primera"),
            ObraDto(id = 2, titulo = "Segunda"),
        )

        assertEquals(listOf(3, 1, 2), dtos.aDominio().map { it.id })
    }
}
