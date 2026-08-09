package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Recuerda de qué página vino cada obra.
 *
 * Sin esto el mediador no sabría qué pedir a continuación: Room le da las obras ya
 * guardadas, pero no de dónde salieron. Al llegar al final de la lista se mira la clave
 * de la última obra y se pide la página siguiente.
 */
@Entity(tableName = "claves_remotas")
data class ClaveRemotaEntidad(
    @PrimaryKey val obraId: Int,
    val paginaAnterior: Int?,
    val paginaSiguiente: Int?,
)
