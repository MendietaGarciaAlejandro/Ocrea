package io.github.mendietagarciaalejandro.ocrea.datos

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.ApiArtic
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import retrofit2.HttpException
import java.io.IOException

/**
 * Paginación de la búsqueda, directa contra la API.
 *
 * A diferencia del catálogo, esto no pasa por Room: los resultados son de usar y tirar
 * y guardarlos ensuciaría la caché del catálogo, que es la que da el modo sin conexión.
 */
class FuenteBusqueda(
    private val api: ApiArtic,
    private val consulta: String,
) : PagingSource<Int, Obra>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Obra> {
        val pagina = params.key ?: PRIMERA_PAGINA

        return try {
            val respuesta = api.buscarObras(
                consulta = consulta,
                pagina = pagina,
                limite = params.loadSize,
            )

            val esUltima = pagina >= respuesta.paginacion.totalPaginas ||
                respuesta.obras.isEmpty() ||
                superaElTope(pagina, params.loadSize)

            LoadResult.Page(
                data = respuesta.obras.aDominio(),
                prevKey = if (pagina == PRIMERA_PAGINA) null else pagina - 1,
                nextKey = if (esUltima) null else pagina + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    /**
     * La API no deja pasar de 10.000 resultados en una búsqueda, da igual cómo combines
     * página y límite. Al llegar ahí se corta en vez de pedir páginas que devolverían error.
     */
    private fun superaElTope(pagina: Int, limite: Int): Boolean =
        pagina * limite >= TOPE_RESULTADOS

    override fun getRefreshKey(state: PagingState<Int, Obra>): Int? =
        state.anchorPosition?.let { posicion ->
            val pagina = state.closestPageToPosition(posicion)
            pagina?.prevKey?.plus(1) ?: pagina?.nextKey?.minus(1)
        }

    companion object {
        const val PRIMERA_PAGINA = 1
        const val TOPE_RESULTADOS = 10_000
    }
}
