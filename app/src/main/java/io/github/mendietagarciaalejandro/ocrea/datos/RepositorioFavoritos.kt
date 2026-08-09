package io.github.mendietagarciaalejandro.ocrea.datos

import io.github.mendietagarciaalejandro.ocrea.datos.local.FavoritoDao
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositorioFavoritos @Inject constructor(
    private val dao: FavoritoDao,
) {

    fun observarFavoritos(): Flow<List<Obra>> =
        dao.observarFavoritos().map { lista -> lista.map { it.aDominio() } }

    fun observarSiEsFavorita(obraId: Int): Flow<Boolean> = dao.observarSiEsFavorita(obraId)

    /**
     * Marca o desmarca, y devuelve cómo queda. El instante se pasa desde fuera para poder
     * fijarlo en los tests en vez de depender del reloj del sistema.
     */
    suspend fun alternar(obra: Obra, ahora: Long = System.currentTimeMillis()): Boolean {
        val eraFavorita = dao.esFavorita(obra.id)

        if (eraFavorita) {
            dao.borrar(obra.id)
        } else {
            dao.guardar(obra.aFavorita(ahora))
        }

        return !eraFavorita
    }
}
