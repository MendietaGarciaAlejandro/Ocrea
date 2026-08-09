package io.github.mendietagarciaalejandro.ocrea.datos

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import io.github.mendietagarciaalejandro.ocrea.datos.local.BaseDatosOcrea
import io.github.mendietagarciaalejandro.ocrea.datos.local.ClaveRemotaEntidad
import io.github.mendietagarciaalejandro.ocrea.datos.local.ObraEntidad
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.ApiArtic
import retrofit2.HttpException
import java.io.IOException

/**
 * Rellena la base de datos con páginas de la API cuando Paging se queda sin obras que
 * mostrar. La interfaz nunca llama aquí: observa Room y ya está.
 */
@OptIn(ExperimentalPagingApi::class)
class MediadorObras(
    private val api: ApiArtic,
    private val baseDatos: BaseDatosOcrea,
) : RemoteMediator<Int, ObraEntidad>() {

    private val obraDao = baseDatos.obraDao()
    private val claveDao = baseDatos.claveRemotaDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ObraEntidad>,
    ): MediatorResult {
        val pagina = when (loadType) {
            LoadType.REFRESH -> PRIMERA_PAGINA

            // El catálogo solo crece hacia abajo: no hay nada antes de la primera página.
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

            LoadType.APPEND -> {
                val ultima = state.lastItemOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)

                claveDao.clavePara(ultima.id)?.paginaSiguiente
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val respuesta = api.obtenerObras(pagina = pagina, limite = state.config.pageSize)
            val obras = respuesta.obras
            val esUltimaPagina = respuesta.paginacion.paginaActual >= respuesta.paginacion.totalPaginas

            baseDatos.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    claveDao.borrarTodo()
                    obraDao.borrarTodo()
                }

                // Al recargar se empieza de cero; si no, se continúa donde quedó la lista.
                val primerOrden = if (loadType == LoadType.REFRESH) {
                    0
                } else {
                    (obraDao.ultimoOrden() ?: -1) + 1
                }

                val paginaAnterior = if (pagina == PRIMERA_PAGINA) null else pagina - 1
                val paginaSiguiente = if (esUltimaPagina) null else pagina + 1

                claveDao.guardar(
                    obras.map { ClaveRemotaEntidad(it.id, paginaAnterior, paginaSiguiente) },
                )
                obraDao.guardar(
                    obras.mapIndexed { indice, dto -> dto.aEntidad(primerOrden + indice) },
                )
            }

            MediatorResult.Success(endOfPaginationReached = esUltimaPagina || obras.isEmpty())
        } catch (e: IOException) {
            // Sin red: Paging lo expone como error y la app sigue mostrando lo cacheado.
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    /**
     * Se lanza una recarga al abrir la app. La colección del museo no cambia cada minuto,
     * pero así se recupera de una caché a medias por un fallo de red.
     */
    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    companion object {
        const val PRIMERA_PAGINA = 1
    }
}
