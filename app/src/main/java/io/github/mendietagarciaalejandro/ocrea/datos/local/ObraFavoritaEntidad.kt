package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una obra marcada como favorita.
 *
 * Guarda una copia completa de los datos en vez de apuntar a la tabla `obras`. Puede
 * parecer duplicado, pero las dos tablas tienen vidas distintas: `obras` es una caché que
 * el mediador vacía en cada recarga, mientras que esto es del usuario y no se toca. Si
 * aquí solo hubiera un id, al recargar el catálogo el favorito quedaría sin datos que
 * mostrar y dejaría de funcionar sin conexión.
 */
@Entity(tableName = "favoritos")
data class ObraFavoritaEntidad(
    @PrimaryKey val id: Int,
    val guardadaEn: Long,
    val titulo: String,
    val artista: String?,
    val fecha: String?,
    val imagenId: String?,
    val tecnica: String?,
    val origen: String?,
    val dimensiones: String?,
    val descripcion: String?,
    val creditos: String?,
    val tipo: String?,
    val departamento: String?,
    val esDominioPublico: Boolean,
)
