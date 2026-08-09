package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    /** Los últimos guardados primero, que es como uno espera encontrarlos. */
    @Query("SELECT * FROM favoritos ORDER BY guardadaEn DESC")
    fun observarFavoritos(): Flow<List<ObraFavoritaEntidad>>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE id = :obraId)")
    fun observarSiEsFavorita(obraId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE id = :obraId)")
    suspend fun esFavorita(obraId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(obra: ObraFavoritaEntidad)

    @Query("DELETE FROM favoritos WHERE id = :obraId")
    suspend fun borrar(obraId: Int)

    @Query("SELECT COUNT(*) FROM favoritos")
    suspend fun contar(): Int
}
