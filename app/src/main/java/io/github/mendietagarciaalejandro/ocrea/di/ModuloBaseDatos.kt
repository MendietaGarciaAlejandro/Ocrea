package io.github.mendietagarciaalejandro.ocrea.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.mendietagarciaalejandro.ocrea.datos.local.BaseDatosOcrea
import io.github.mendietagarciaalejandro.ocrea.datos.local.ClaveRemotaDao
import io.github.mendietagarciaalejandro.ocrea.datos.local.FavoritoDao
import io.github.mendietagarciaalejandro.ocrea.datos.local.MIGRACION_1_2
import io.github.mendietagarciaalejandro.ocrea.datos.local.ObraDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloBaseDatos {

    @Provides
    @Singleton
    fun proporcionarBaseDatos(@ApplicationContext contexto: Context): BaseDatosOcrea =
        Room.databaseBuilder(contexto, BaseDatosOcrea::class.java, BaseDatosOcrea.NOMBRE)
            // Nada de borrado destructivo: en esta base de datos ya hay favoritos del
            // usuario, y esos no se pueden perder al actualizar la app.
            .addMigrations(MIGRACION_1_2)
            .build()

    @Provides
    fun proporcionarObraDao(baseDatos: BaseDatosOcrea): ObraDao = baseDatos.obraDao()

    @Provides
    fun proporcionarClaveRemotaDao(baseDatos: BaseDatosOcrea): ClaveRemotaDao =
        baseDatos.claveRemotaDao()

    @Provides
    fun proporcionarFavoritoDao(baseDatos: BaseDatosOcrea): FavoritoDao =
        baseDatos.favoritoDao()
}
