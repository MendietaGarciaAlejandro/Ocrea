package io.github.mendietagarciaalejandro.ocrea.datos

import io.github.mendietagarciaalejandro.ocrea.datos.remoto.ApiArtic
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.ObraDto
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.PaginacionDto
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.RespuestaObraDto
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.RespuestaObrasDto
import java.io.IOException

/**
 * API de mentira con un catálogo controlado, para poder comprobar la paginación sin
 * depender de la red ni del museo.
 */
class ApiArticFalsa(
    private val totalObras: Int = 75,
    var fallarConError: Boolean = false,
) : ApiArtic {

    /** Peticiones recibidas, en orden, como pares (pagina, limite). */
    val peticiones = mutableListOf<Pair<Int, Int>>()

    override suspend fun obtenerObras(pagina: Int, limite: Int, campos: String): RespuestaObrasDto {
        if (fallarConError) throw IOException("sin conexión")

        peticiones += pagina to limite

        val desde = (pagina - 1) * limite
        val hasta = minOf(desde + limite, totalObras)
        val obras = if (desde >= totalObras) {
            emptyList()
        } else {
            (desde until hasta).map { indice ->
                ObraDto(id = indice + 1, titulo = "Obra ${indice + 1}", imagenId = "img-${indice + 1}")
            }
        }

        val totalPaginas = (totalObras + limite - 1) / limite

        return RespuestaObrasDto(
            paginacion = PaginacionDto(
                total = totalObras,
                limite = limite,
                paginaActual = pagina,
                totalPaginas = totalPaginas,
            ),
            obras = obras,
        )
    }

    /** Consultas recibidas, para comprobar que el debounce filtra lo que debe. */
    val busquedas = mutableListOf<String>()

    /** Cuántas obras devuelve una búsqueda; por defecto, las mismas que el catálogo. */
    var resultadosBusqueda: Int? = null

    override suspend fun buscarObras(
        consulta: String,
        pagina: Int,
        limite: Int,
        campos: String,
    ): RespuestaObrasDto {
        if (fallarConError) throw IOException("sin conexión")

        busquedas += consulta

        val total = resultadosBusqueda ?: totalObras
        val desde = (pagina - 1) * limite
        val hasta = minOf(desde + limite, total)
        val obras = if (desde >= total) {
            emptyList()
        } else {
            (desde until hasta).map { indice ->
                ObraDto(id = indice + 1, titulo = "$consulta ${indice + 1}")
            }
        }

        return RespuestaObrasDto(
            paginacion = PaginacionDto(
                total = total,
                limite = limite,
                paginaActual = pagina,
                totalPaginas = if (total == 0) 0 else (total + limite - 1) / limite,
            ),
            obras = obras,
        )
    }

    override suspend fun obtenerObra(id: Int, campos: String): RespuestaObraDto =
        RespuestaObraDto(ObraDto(id = id, titulo = "Obra $id", imagenId = "img-$id"))
}
