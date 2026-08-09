package io.github.mendietagarciaalejandro.ocrea.datos

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import io.github.mendietagarciaalejandro.ocrea.datos.local.BaseDatosOcrea
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.ApiArtic
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositorioObras @Inject constructor(
    private val api: ApiArtic,
    private val baseDatos: BaseDatosOcrea,
) {

    /**
     * Catálogo paginado. Sale siempre de Room; el mediador se encarga de traer páginas
     * nuevas cuando hacen falta.
     */
    @OptIn(ExperimentalPagingApi::class)
    fun paginarCatalogo(): Flow<PagingData<Obra>> = Pager(
        config = PagingConfig(
            pageSize = TAMANO_PAGINA,
            // Sin esto la primera carga pediria el triple de golpe y descuadraria
            // el numero de pagina que guardan las claves remotas.
            initialLoadSize = TAMANO_PAGINA,
            prefetchDistance = 10,
            enablePlaceholders = false,
        ),
        remoteMediator = MediadorObras(api, baseDatos),
        pagingSourceFactory = { baseDatos.obraDao().paginarObras() },
    ).flow.map { pagina -> pagina.map { it.aDominio() } }

    /**
     * Detalle de una obra. Se pide a la API porque el listado solo trae cuatro campos,
     * y de paso se refresca la copia local.
     */
    suspend fun obtenerDetalle(id: Int): Obra {
        val dto = api.obtenerObra(id).obra
        val orden = baseDatos.obraDao().obtenerObra(id)?.orden ?: 0

        baseDatos.obraDao().guardar(listOf(dto.aEntidad(orden)))

        return dto.aDominio()
    }

    companion object {
        const val TAMANO_PAGINA = 30
    }
}
