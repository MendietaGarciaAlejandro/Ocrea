package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ObraDao {

    /**
     * Room genera el PagingSource: Paging pide trozos de esta consulta segun hace falta y
     * se resuscribe solo cuando la tabla cambia.
     */
    @Query("SELECT * FROM obras ORDER BY orden ASC")
    fun paginarObras(): PagingSource<Int, ObraEntidad>

    @Query("SELECT * FROM obras WHERE id = :id")
    fun observarObra(id: Int): Flow<ObraEntidad?>

    @Query("SELECT * FROM obras WHERE id = :id")
    suspend fun obtenerObra(id: Int): ObraEntidad?

    /** Al recargar una página ya vista se sustituyen las obras con datos frescos. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(obras: List<ObraEntidad>)

    @Query("SELECT MAX(orden) FROM obras")
    suspend fun ultimoOrden(): Int?

    @Query("SELECT COUNT(*) FROM obras")
    suspend fun contar(): Int

    @Query("DELETE FROM obras")
    suspend fun borrarTodo()
}
