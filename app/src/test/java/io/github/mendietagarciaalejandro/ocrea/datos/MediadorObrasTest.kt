package io.github.mendietagarciaalejandro.ocrea.datos

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import io.github.mendietagarciaalejandro.ocrea.datos.local.BaseDatosOcrea
import io.github.mendietagarciaalejandro.ocrea.datos.local.ObraEntidad
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
class MediadorObrasTest {

    private lateinit var baseDatos: BaseDatosOcrea
    private lateinit var api: ApiArticFalsa

    private val tamanoPagina = 30

    @Before
    fun preparar() {
        baseDatos = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            BaseDatosOcrea::class.java,
        ).allowMainThreadQueries().build()

        api = ApiArticFalsa(totalObras = 75)
    }

    @After
    fun cerrar() {
        baseDatos.close()
    }

    private fun mediador() = MediadorObras(api, baseDatos)

    private fun estadoVacio() = PagingState<Int, ObraEntidad>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = tamanoPagina),
        leadingPlaceholderCount = 0,
    )

    private fun estadoCon(obras: List<ObraEntidad>) = PagingState<Int, ObraEntidad>(
        pages = listOf(
            PagingSource.LoadResult.Page(
                data = obras,
                prevKey = null,
                nextKey = null,
            ),
        ),
        anchorPosition = null,
        config = PagingConfig(pageSize = tamanoPagina),
        leadingPlaceholderCount = 0,
    )

    @Test
    fun `el refresco trae la primera pagina y la guarda`() = runTest {
        val resultado = mediador().load(LoadType.REFRESH, estadoVacio())

        assertTrue(resultado is RemoteMediator.MediatorResult.Success)
        assertEquals(1 to tamanoPagina, api.peticiones.single())

        val guardadas = baseDatos.obraDao().ultimoOrden()
        assertEquals(tamanoPagina - 1, guardadas)
    }

    @Test
    fun `quedan mas paginas cuando el catalogo no se ha agotado`() = runTest {
        val resultado = mediador().load(LoadType.REFRESH, estadoVacio())

        // 75 obras de 30 en 30 son 3 paginas: tras la primera queda recorrido.
        assertEquals(
            false,
            (resultado as RemoteMediator.MediatorResult.Success).endOfPaginationReached,
        )
    }

    @Test
    fun `avanzar pide la pagina siguiente y continua el orden`() = runTest {
        val mediador = mediador()
        mediador.load(LoadType.REFRESH, estadoVacio())

        val primeras = baseDatos.obraDao().obtenerObra(30)
        assertEquals(29, primeras?.orden)

        mediador.load(LoadType.APPEND, estadoCon(listOf(checkNotNull(primeras))))

        assertEquals(listOf(1 to 30, 2 to 30), api.peticiones)
        // La segunda pagina continua numerando, no reinicia.
        assertEquals(59, baseDatos.obraDao().ultimoOrden())
    }

    @Test
    fun `al llegar al final se marca el fin de la paginacion`() = runTest {
        val mediador = mediador()
        mediador.load(LoadType.REFRESH, estadoVacio())
        mediador.load(LoadType.APPEND, estadoCon(listOf(checkNotNull(baseDatos.obraDao().obtenerObra(30)))))

        val ultima = mediador.load(
            LoadType.APPEND,
            estadoCon(listOf(checkNotNull(baseDatos.obraDao().obtenerObra(60)))),
        )

        assertTrue(
            (ultima as RemoteMediator.MediatorResult.Success).endOfPaginationReached,
        )
        assertEquals(75, baseDatos.obraDao().contar())
    }

    @Test
    fun `la ultima obra no tiene pagina siguiente`() = runTest {
        val mediador = mediador()
        mediador.load(LoadType.REFRESH, estadoVacio())
        mediador.load(LoadType.APPEND, estadoCon(listOf(checkNotNull(baseDatos.obraDao().obtenerObra(30)))))
        mediador.load(LoadType.APPEND, estadoCon(listOf(checkNotNull(baseDatos.obraDao().obtenerObra(60)))))

        val clave = baseDatos.claveRemotaDao().clavePara(75)

        assertNull(checkNotNull(clave).paginaSiguiente)
    }

    @Test
    fun `recargar vacia lo anterior en vez de duplicarlo`() = runTest {
        val mediador = mediador()
        mediador.load(LoadType.REFRESH, estadoVacio())
        mediador.load(LoadType.APPEND, estadoCon(listOf(checkNotNull(baseDatos.obraDao().obtenerObra(30)))))
        assertEquals(60, baseDatos.obraDao().contar())

        mediador.load(LoadType.REFRESH, estadoVacio())

        assertEquals(tamanoPagina, baseDatos.obraDao().contar())
        assertEquals(tamanoPagina - 1, baseDatos.obraDao().ultimoOrden())
    }

    @Test
    fun `no hay nada que cargar por delante del catalogo`() = runTest {
        val resultado = mediador().load(LoadType.PREPEND, estadoVacio())

        assertTrue(
            (resultado as RemoteMediator.MediatorResult.Success).endOfPaginationReached,
        )
        assertTrue("PREPEND no deberia llamar a la api", api.peticiones.isEmpty())
    }

    @Test
    fun `un fallo de red se comunica como error y no tumba la app`() = runTest {
        api.fallarConError = true

        val resultado = mediador().load(LoadType.REFRESH, estadoVacio())

        assertTrue(resultado is RemoteMediator.MediatorResult.Error)
        assertTrue((resultado as RemoteMediator.MediatorResult.Error).throwable is IOException)
    }

    @Test
    fun `lo ya descargado sobrevive a una caida de red`() = runTest {
        val mediador = mediador()
        mediador.load(LoadType.REFRESH, estadoVacio())

        api.fallarConError = true
        mediador.load(LoadType.APPEND, estadoCon(listOf(checkNotNull(baseDatos.obraDao().obtenerObra(30)))))

        // La primera pagina sigue en local: la app puede seguir mostrandola sin conexion.
        assertEquals(tamanoPagina, baseDatos.obraDao().contar())
    }

}
