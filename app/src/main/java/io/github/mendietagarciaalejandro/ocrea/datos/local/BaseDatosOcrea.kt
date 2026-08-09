package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ObraEntidad::class, ClaveRemotaEntidad::class],
    version = 1,
    exportSchema = false,
)
abstract class BaseDatosOcrea : RoomDatabase() {
    abstract fun obraDao(): ObraDao
    abstract fun claveRemotaDao(): ClaveRemotaDao

    companion object {
        const val NOMBRE = "ocrea.db"
    }
}
