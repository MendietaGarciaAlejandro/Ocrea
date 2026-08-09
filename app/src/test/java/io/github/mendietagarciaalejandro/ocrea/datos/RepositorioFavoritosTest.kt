package io.github.mendietagarciaalejandro.ocrea.datos

import androidx.room.Room
import io.github.mendietagarciaalejandro.ocrea.datos.local.BaseDatosOcrea
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class RepositorioFavoritosTest {

    private lateinit var baseDatos: BaseDatosOcrea
    private lateinit var repositorio: RepositorioFavoritos

    private val obra = Obra(
        id = 27992,
        titulo = "A Sunday on La Grande Jatte",
        artista = "Georges Seurat",
        fecha = "1884",
        imagenId = "abc-123",
        tecnica = "Óleo sobre lienzo",
    )

    @Before
    fun preparar() {
        baseDatos = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            BaseDatosOcrea::class.java,
        ).allowMainThreadQueries().build()

        repositorio = RepositorioFavoritos(baseDatos.favoritoDao())
    }

    @After
    fun cerrar() {
        baseDatos.close()
    }

    @Test
    fun `al principio no hay favoritos`() = runTest {
        assertTrue(repositorio.observarFavoritos().first().isEmpty())
        assertFalse(repositorio.observarSiEsFavorita(obra.id).first())
    }

    @Test
    fun `alternar guarda y devuelve que ahora es favorita`() = runTest {
        val quedaMarcada = repositorio.alternar(obra, ahora = 1_000L)

        assertTrue(quedaMarcada)
        assertTrue(repositorio.observarSiEsFavorita(obra.id).first())
        assertEquals(1, baseDatos.favoritoDao().contar())
    }

    @Test
    fun `alternar dos veces la quita`() = runTest {
        repositorio.alternar(obra, ahora = 1_000L)
        val quedaMarcada = repositorio.alternar(obra, ahora = 2_000L)

        assertFalse(quedaMarcada)
        assertEquals(0, baseDatos.favoritoDao().contar())
    }

    @Test
    fun `el favorito guarda todos los datos y no solo el id`() = runTest {
        repositorio.alternar(obra, ahora = 1_000L)

        val guardada = repositorio.observarFavoritos().first().single()

        // Esto es lo que permite que el favorito siga viéndose aunque el catálogo se vacíe.
        assertEquals(obra.titulo, guardada.titulo)
        assertEquals(obra.artista, guardada.artista)
        assertEquals(obra.tecnica, guardada.tecnica)
        assertEquals(obra.imagenId, guardada.imagenId)
    }

    @Test
    fun `el favorito sobrevive a que se vacie el catalogo`() = runTest {
        baseDatos.obraDao().guardar(listOf(obra.aEntidadDePrueba()))
        repositorio.alternar(obra, ahora = 1_000L)

        // El mediador vacía la tabla de obras en cada recarga del catálogo.
        baseDatos.obraDao().borrarTodo()

        assertEquals(0, baseDatos.obraDao().contar())
        assertEquals(1, baseDatos.favoritoDao().contar())
        assertEquals(obra.titulo, repositorio.observarFavoritos().first().single().titulo)
    }

    @Test
    fun `los mas recientes salen primero`() = runTest {
        val antigua = obra.copy(id = 1, titulo = "La primera")
        val reciente = obra.copy(id = 2, titulo = "La ultima")

        repositorio.alternar(antigua, ahora = 1_000L)
        repositorio.alternar(reciente, ahora = 5_000L)

        assertEquals(
            listOf("La ultima", "La primera"),
            repositorio.observarFavoritos().first().map { it.titulo },
        )
    }
}

private fun Obra.aEntidadDePrueba() =
    io.github.mendietagarciaalejandro.ocrea.datos.local.ObraEntidad(
        id = id,
        orden = 0,
        titulo = titulo,
        artista = artista,
        fecha = fecha,
        imagenId = imagenId,
        tecnica = tecnica,
        origen = origen,
        dimensiones = dimensiones,
        descripcion = descripcion,
        creditos = creditos,
        tipo = tipo,
        departamento = departamento,
        esDominioPublico = esDominioPublico,
    )
