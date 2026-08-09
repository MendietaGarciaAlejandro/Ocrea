package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una obra guardada en local.
 *
 * El campo [orden] existe porque la API no garantiza un orden estable entre peticiones:
 * si se ordenara por id o por título, la lista bailaría al recargar. Se guarda la posición
 * con la que llegó cada obra y se pagina por ahí.
 */
@Entity(tableName = "obras")
data class ObraEntidad(
    @PrimaryKey val id: Int,
    val orden: Int,
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
