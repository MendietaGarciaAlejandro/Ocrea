package io.github.mendietagarciaalejandro.ocrea.datos.remoto

import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.RespuestaObraDto
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.RespuestaObrasDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API publica del Art Institute of Chicago.
 *
 * Se pide siempre el parametro "fields" para que la respuesta traiga solo lo que se usa:
 * el objeto completo de una obra tiene mas de cien campos y la mayoria no hacen falta.
 * Ademas el museo limita a 60 peticiones por minuto, asi que conviene no malgastarlas.
 */
interface ApiArtic {

    @GET("artworks")
    suspend fun obtenerObras(
        @Query("page") pagina: Int,
        @Query("limit") limite: Int,
        @Query("fields") campos: String = CAMPOS_LISTADO,
    ): RespuestaObrasDto

    @GET("artworks/search")
    suspend fun buscarObras(
        @Query("q") consulta: String,
        @Query("page") pagina: Int,
        @Query("limit") limite: Int,
        @Query("fields") campos: String = CAMPOS_LISTADO,
    ): RespuestaObrasDto

    @GET("artworks/{id}")
    suspend fun obtenerObra(
        @Path("id") id: Int,
        @Query("fields") campos: String = CAMPOS_DETALLE,
    ): RespuestaObraDto

    companion object {
        const val URL_BASE = "https://api.artic.edu/api/v1/"

        /** Maximo que admite la API por pagina. */
        const val LIMITE_MAXIMO = 100

        /** Lo justo para pintar una tarjeta en la cuadricula. */
        const val CAMPOS_LISTADO = "id,title,artist_title,date_display,image_id"

        /** Todo lo que muestra la pantalla de detalle. */
        const val CAMPOS_DETALLE =
            "id,title,artist_title,artist_display,date_display,image_id,medium_display," +
                "place_of_origin,dimensions,description,credit_line,artwork_type_title," +
                "department_title,is_public_domain"
    }
}
