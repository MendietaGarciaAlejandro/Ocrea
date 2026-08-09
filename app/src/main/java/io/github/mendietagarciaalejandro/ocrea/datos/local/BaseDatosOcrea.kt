package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ObraEntidad::class, ClaveRemotaEntidad::class, ObraFavoritaEntidad::class],
    version = 2,
    // Los esquemas se versionan en el repo: hacen falta para escribir y probar migraciones.
    exportSchema = true,
)
abstract class BaseDatosOcrea : RoomDatabase() {
    abstract fun obraDao(): ObraDao
    abstract fun claveRemotaDao(): ClaveRemotaDao
    abstract fun favoritoDao(): FavoritoDao

    companion object {
        const val NOMBRE = "ocrea.db"
    }
}
