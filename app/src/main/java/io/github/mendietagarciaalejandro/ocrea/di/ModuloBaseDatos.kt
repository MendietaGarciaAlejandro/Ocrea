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
import io.github.mendietagarciaalejandro.ocrea.datos.local.ObraDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloBaseDatos {

    @Provides
    @Singleton
    fun proporcionarBaseDatos(@ApplicationContext contexto: Context): BaseDatosOcrea =
        Room.databaseBuilder(contexto, BaseDatosOcrea::class.java, BaseDatosOcrea.NOMBRE)
            // El contenido es una caché de la API: si cambia el esquema, se descarta y
            // se vuelve a bajar. No hay nada del usuario que perder todavía.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun proporcionarObraDao(baseDatos: BaseDatosOcrea): ObraDao = baseDatos.obraDao()

    @Provides
    fun proporcionarClaveRemotaDao(baseDatos: BaseDatosOcrea): ClaveRemotaDao =
        baseDatos.claveRemotaDao()
}
