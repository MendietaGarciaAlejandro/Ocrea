package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ClaveRemotaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(claves: List<ClaveRemotaEntidad>)

    @Query("SELECT * FROM claves_remotas WHERE obraId = :obraId")
    suspend fun clavePara(obraId: Int): ClaveRemotaEntidad?

    @Query("DELETE FROM claves_remotas")
    suspend fun borrarTodo()
}
