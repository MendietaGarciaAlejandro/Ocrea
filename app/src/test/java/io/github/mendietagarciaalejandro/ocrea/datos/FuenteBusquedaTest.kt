package io.github.mendietagarciaalejandro.ocrea.datos

import androidx.paging.PagingSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class FuenteBusquedaTest {

    private suspend fun cargar(
        fuente: FuenteBusqueda,
        clave: Int?,
        tamano: Int = 30,
    ): PagingSource.LoadResult<Int, io.github.mendietagarciaalejandro.ocrea.dominio.Obra> =
        fuente.load(PagingSource.LoadParams.Refresh(clave, tamano, false))

    @Test
    fun `la primera pagina no tiene anterior`() = runTest {
        val api = ApiArticFalsa(totalObras = 75)
        val fuente = FuenteBusqueda(api, "monet")

        val resultado = cargar(fuente, null) as PagingSource.LoadResult.Page

        assertEquals(30, resultado.data.size)
        assertNull(resultado.prevKey)
        assertEquals(2, resultado.nextKey)
        assertEquals("monet", api.busquedas.single())
    }

    @Test
    fun `la ultima pagina no tiene siguiente`() = runTest {
        val api = ApiArticFalsa(totalObras = 75)
        val fuente = FuenteBusqueda(api, "monet")

        val resultado = cargar(fuente, 3) as PagingSource.LoadResult.Page

        assertEquals(15, resultado.data.size)
        assertEquals(2, resultado.prevKey)
        assertNull(resultado.nextKey)
    }

    @Test
    fun `una busqueda sin resultados devuelve pagina vacia y sin continuacion`() = runTest {
        val api = ApiArticFalsa(totalObras = 75).apply { resultadosBusqueda = 0 }
        val fuente = FuenteBusqueda(api, "asdfghjkl")

        val resultado = cargar(fuente, null) as PagingSource.LoadResult.Page

        assertTrue(resultado.data.isEmpty())
        assertNull(resultado.nextKey)
    }

    @Test
    fun `no se pasa del tope de diez mil resultados que impone la api`() = runTest {
        // Con paginas de 100, la 100 ya alcanza el tope: no debe pedirse la siguiente.
        val api = ApiArticFalsa(totalObras = 500_000)
        val fuente = FuenteBusqueda(api, "arte")

        val resultado = cargar(fuente, 100, tamano = 100) as PagingSource.LoadResult.Page

        assertNull("la api rechazaria pedir mas alla de 10.000", resultado.nextKey)
    }

    @Test
    fun `antes del tope si hay pagina siguiente`() = runTest {
        val api = ApiArticFalsa(totalObras = 500_000)
        val fuente = FuenteBusqueda(api, "arte")

        val resultado = cargar(fuente, 50, tamano = 100) as PagingSource.LoadResult.Page

        assertEquals(51, resultado.nextKey)
    }

    @Test
    fun `un fallo de red se propaga como error de paginacion`() = runTest {
        val api = ApiArticFalsa().apply { fallarConError = true }
        val fuente = FuenteBusqueda(api, "monet")

        val resultado = cargar(fuente, null)

        assertTrue(resultado is PagingSource.LoadResult.Error)
        assertTrue((resultado as PagingSource.LoadResult.Error).throwable is IOException)
    }

    @Test
    fun `los resultados llegan ya como modelo de dominio`() = runTest {
        val api = ApiArticFalsa(totalObras = 5)
        val fuente = FuenteBusqueda(api, "monet")

        val resultado = cargar(fuente, null) as PagingSource.LoadResult.Page

        assertNotNull(resultado.data.first().titulo)
        assertTrue(resultado.data.first().titulo.startsWith("monet"))
    }
}
