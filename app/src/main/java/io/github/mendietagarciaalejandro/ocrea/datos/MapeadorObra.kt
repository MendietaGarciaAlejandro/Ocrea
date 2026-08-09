package io.github.mendietagarciaalejandro.ocrea.datos

import io.github.mendietagarciaalejandro.ocrea.datos.local.ObraEntidad
import io.github.mendietagarciaalejandro.ocrea.datos.local.ObraFavoritaEntidad
import io.github.mendietagarciaalejandro.ocrea.datos.remoto.dto.ObraDto
import io.github.mendietagarciaalejandro.ocrea.dominio.Obra

fun ObraDto.aDominio(): Obra = Obra(
    id = id,
    titulo = titulo,
    artista = artista?.enBlancoANulo(),
    fecha = fecha?.enBlancoANulo(),
    imagenId = imagenId?.enBlancoANulo(),
    tecnica = tecnica?.enBlancoANulo(),
    origen = origen?.enBlancoANulo(),
    dimensiones = dimensiones?.enBlancoANulo(),
    descripcion = descripcion?.aTextoPlano()?.enBlancoANulo(),
    creditos = creditos?.enBlancoANulo(),
    tipo = tipo?.enBlancoANulo(),
    departamento = departamento?.enBlancoANulo(),
    esDominioPublico = esDominioPublico,
)

fun List<ObraDto>.aDominio(): List<Obra> = map { it.aDominio() }

/** El [orden] es la posición global con la que llegó la obra, no viene de la API. */
fun ObraDto.aEntidad(orden: Int): ObraEntidad = ObraEntidad(
    id = id,
    orden = orden,
    titulo = titulo,
    artista = artista?.enBlancoANulo(),
    fecha = fecha?.enBlancoANulo(),
    imagenId = imagenId?.enBlancoANulo(),
    tecnica = tecnica?.enBlancoANulo(),
    origen = origen?.enBlancoANulo(),
    dimensiones = dimensiones?.enBlancoANulo(),
    descripcion = descripcion?.aTextoPlano()?.enBlancoANulo(),
    creditos = creditos?.enBlancoANulo(),
    tipo = tipo?.enBlancoANulo(),
    departamento = departamento?.enBlancoANulo(),
    esDominioPublico = esDominioPublico,
)

fun Obra.aFavorita(guardadaEn: Long): ObraFavoritaEntidad = ObraFavoritaEntidad(
    id = id,
    guardadaEn = guardadaEn,
    titulo = titulo,
    artista = artista,
    fecha = fecha,
    imagenId = imagenId,
    tecnica = tecnica,
    origen = origen,
    dimensiones = dimensiones,
    descripcion = descripcion,
    creditos = creditos,
    tipo = tipo,
    departamento = departamento,
    esDominioPublico = esDominioPublico,
)

fun ObraFavoritaEntidad.aDominio(): Obra = Obra(
    id = id,
    titulo = titulo,
    artista = artista,
    fecha = fecha,
    imagenId = imagenId,
    tecnica = tecnica,
    origen = origen,
    dimensiones = dimensiones,
    descripcion = descripcion,
    creditos = creditos,
    tipo = tipo,
    departamento = departamento,
    esDominioPublico = esDominioPublico,
)

fun ObraEntidad.aDominio(): Obra = Obra(
    id = id,
    titulo = titulo,
    artista = artista,
    fecha = fecha,
    imagenId = imagenId,
    tecnica = tecnica,
    origen = origen,
    dimensiones = dimensiones,
    descripcion = descripcion,
    creditos = creditos,
    tipo = tipo,
    departamento = departamento,
    esDominioPublico = esDominioPublico,
)

/** La API a veces manda cadenas vacías donde deberia mandar null. */
private fun String.enBlancoANulo(): String? = trim().takeIf { it.isNotEmpty() }

private val ETIQUETA = Regex("<[^>]+>")
private val ESPACIOS_SEGUIDOS = Regex("[ \\t]{2,}")
private val SALTOS_SEGUIDOS = Regex("\n{3,}")

/**
 * Las descripciones del museo llegan en HTML (<p>, <em>, enlaces...). Se limpian aquí,
 * en la capa de datos, para que el dominio y la interfaz manejen texto plano.
 *
 * No es un parser de HTML completo ni pretende serlo: basta para el marcado sencillo que
 * usa esta API. Si algún día hiciera falta pintar cursivas o enlaces, habria que cambiar
 * de enfoque y quedarse el HTML.
 */
private fun String.aTextoPlano(): String = this
    .replace("<br>", "\n", ignoreCase = true)
    .replace("<br/>", "\n", ignoreCase = true)
    .replace("<br />", "\n", ignoreCase = true)
    .replace("</p>", "\n\n", ignoreCase = true)
    .replace(ETIQUETA, "")
    .decodificarEntidades()
    .replace("\r\n", "\n")
    .replace(ESPACIOS_SEGUIDOS, " ")
    .replace(SALTOS_SEGUIDOS, "\n\n")
    .trim()

private fun String.decodificarEntidades(): String = this
    .replace("&nbsp;", " ")
    .replace("&#160;", " ")
    .replace("&lt;", "<")
    .replace("&gt;", ">")
    .replace("&quot;", "\"")
    .replace("&#39;", "'")
    .replace("&rsquo;", "’")
    .replace("&lsquo;", "‘")
    .replace("&ldquo;", "“")
    .replace("&rdquo;", "”")
    .replace("&mdash;", "—")
    .replace("&ndash;", "–")
    // El &amp; va el ultimo: si no, desharia las entidades ya decodificadas.
    .replace("&amp;", "&")
