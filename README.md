# Ocrea

App Android para explorar la colección del Art Institute of Chicago. Son unas 130.000
obras, con buscador por texto, filtros por autor, época y departamento, ficha de cada pieza,
visor con zoom para mirarlas de cerca y favoritos que se pueden consultar sin conexión.

La API la elegí precisamente por eso, porque tiene volumen de verdad. Con veinte resultados
de prueba cualquier cosa parece que funciona; con 130.000 y un límite de 60 peticiones por
minuto te toca pensarte la caché y la paginación en serio, que era lo que quería practicar.

El nombre viene del ocre, el pigmento de las pinturas rupestres.

## Stack

- Kotlin y Jetpack Compose con Material 3
- Paging 3 con `RemoteMediator` sobre Room
- Retrofit con kotlinx.serialization
- Hilt para inyección de dependencias
- Coil para las imágenes
- Tests con JUnit y Robolectric

## Cómo ejecutarlo

No hace falta ninguna clave ni configuración: la API del museo es abierta.

```
./gradlew installDebug
```

Los tests de JVM van con `./gradlew testDebugUnitTest`. Los de migración están en
`androidTest` porque necesitan leer los esquemas exportados desde los assets de la
instrumentación, así que requieren un dispositivo o emulador.

## Cómo está montado

```
UI (Compose)  ──observa──▶  Room  ◀──rellena──  RemoteMediator  ──▶  API
```

La interfaz nunca habla con la red. Observa Room y ya está. Cuando Paging se queda sin
obras que mostrar, avisa al mediador, este pide la siguiente página, la guarda en una
transacción y Room emite los datos nuevos hacia arriba. De ahí sale el modo sin conexión
casi gratis: lo que ya has visto sigue estando.

Las capas son las de siempre: `datos` (red, Room y repositorios), `dominio` (modelos sin
dependencias de Android) y `ui`. Los DTO no salen de la capa de datos: se convierten a
modelo de dominio en el mapeador, que además es donde se limpia el HTML y se construye la
URL de las imágenes.

Los identificadores están en español. Lo del framework se queda como está, claro, así que
el código es híbrido; es una decisión de estilo y no me molesta.

## Decisiones que me parecen las importantes

### La tabla de obras guarda el orden con el que llegaron

La API no garantiza que dos peticiones devuelvan las obras en el mismo orden. Si paginara
ordenando por id o por título, la lista bailaría al recargar y aparecerían huecos o
repetidos. Así que guardo la posición con la que llegó cada obra y pagino por ese campo.

### `initialLoadSize` igual al tamaño de página

Esto me costó entenderlo. Por defecto Paging pide el triple de elementos en la primera
carga. El problema es que mis claves remotas guardan un número de página: si la primera
petición trae 90 obras pero las apunto todas como "página 1", la siguiente sería la 2 y me
saltaría sesenta obras. Igualando los dos tamaños la cuenta cuadra.

### Los filtros se combinan con una consulta `bool`

El buscador del museo es un Elasticsearch y acepta filtros con notación de corchetes. La
primera versión mandaba el texto en `q` y el rango de años aparte, y los totales salían
disparados: resulta que así el texto solo puntúa resultados, no filtra. Metiendo cada
condición en `query[bool][must][N]` sí se suman. Buscar Picasso desde 1930 pasa de decenas
de miles de resultados a 243, que son los que tiene el museo.

### La búsqueda no se guarda en Room

El catálogo se cachea porque es lo que permite abrir la app sin conexión. Los resultados
de búsqueda son otra cosa: los miras y los tiras. Si los metiera en la misma tabla se
mezclarían con el catálogo y me cargaría tanto el orden como el offline. Por eso hay dos
caminos distintos: mediador con Room para el catálogo, y un `PagingSource` que va directo
a la red para buscar.

La contrapartida es que buscar sin conexión no funciona, y me parece un intercambio justo.

### Un favorito guarda la obra entera, no su id

Puede parecer que duplico datos, y es verdad, pero es a propósito. El mediador vacía la
tabla de obras en cada recarga del catálogo. Si el favorito fuese solo un id, al recargar
me quedaría un identificador huérfano y el favorito dejaría de verse sin conexión, que es
justo para lo que sirve. Las dos tablas tienen vidas distintas, así que no se comparten.

### Migraciones de verdad desde que hay favoritos

Al principio la base de datos usaba `fallbackToDestructiveMigration`: si cambiaba el
esquema, se borraba todo y se volvía a bajar. Con una caché de la API eso es perfectamente
razonable. En cuanto guardé el primer favorito dejó de serlo, porque eso ya es del usuario
y no se puede recuperar de ningún sitio. Lo cambié por una migración escrita a mano y dejé
los esquemas versionados en `app/schemas` para poder comprobarla.

### El teclado no dispara una petición por letra

La búsqueda espera 350 ms desde la última tecla y descarta consultas de una sola letra. Lo
medí en el móvil: escribir "monet" letra a letra genera **una** petición. Sin eso serían
cuatro, y con el tope de 60 por minuto que pone el museo se nota.

### El visor pide el doble de resolución que el detalle

La ficha de la obra usa los 843 píxeles de ancho que recomienda el museo, porque a ese
tamaño sirven la imagen ya cacheada. Para el visor con zoom se queda corto: al acercarse
se ve el pixelado antes que la pincelada, así que ahí se piden 1686. Solo se paga esa
descarga si el usuario abre el visor.

El fondo del visor es negro y no sigue al tema, a diferencia del resto de la app. Es lo que
hacen todos los visores de imágenes y aquí importa más de lo normal: un marco claro compite
con los colores del cuadro.

### Sin Material You

Material You recolorearía la interfaz según el fondo de pantalla del móvil. En una app que
enseña cuadros eso pelea con las obras, así que dejé una paleta de tierras fija y apagada.
El color lo pone lo que hay dentro del marco.

## Cosas que aprendí a base de romperlas

**Hilt no soportaba AGP 9.** Al añadirlo, la build falló con "Android BaseExtension not
found". La última versión que encontré era de abril de 2025 y llegué a bajar el proyecto a
AGP 8 para poder usarlo. Resulta que había una versión más reciente que sí funciona: el
buscador de Maven Central me estaba dando datos viejos, y consultando el `maven-metadata.xml`
directamente aparecía. Deshice el downgrade. Ahora miro siempre esos ficheros.

**La nulabilidad no era paranoia.** Puse casi todos los campos del DTO como nulables porque
la documentación lo daba a entender, y al capturar respuestas reales para los tests me
encontré con que dos de cada tres obras venían sin autor. Si los hubiera declarado no
nulables, la app se caería al deserializar la primera pantalla.

**Actualizar dependencias rompió la compilación.** Subí todas las librerías a la vez y
dejó de compilar el módulo de Kotlin. El Compose BOM nuevo pedía una versión de Kotlin más
moderna que la que traía la plantilla de Android Studio. Por cierto, esa plantilla venía con
`core-ktx` de 2023 conviviendo con un BOM de 2026, así que revisar el catálogo de versiones
antes del primer commit se ha convertido en parte de mi rutina.

**Los iconos de Material están congelados.** El artefacto dejó de publicarse en la 1.7.8
mientras Compose va por la 1.11, así que va con versión fija fuera del BOM. Solo uso tres
iconos; si necesitara más me plantearía dibujarlos.

## Lo que no hace

- No se puede buscar sin conexión, por lo que explico arriba.
- Los favoritos viven solo en el móvil. No hay cuenta ni sincronización.
- No hay "deslizar para recargar". El catálogo se refresca al abrir la app.
- El limpiador de HTML de las descripciones es apañado: quita etiquetas y decodifica las
  entidades más comunes, nada más. Si algún día quisiera pintar cursivas o enlaces habría
  que cambiar de enfoque y conservar el marcado.
- Robolectric no soporta todavía la API 37, que es la que compila la app, así que los tests
  de JVM corren simulando la 34. Como solo prueban la capa de datos, no me preocupa.
- No hay tests de interfaz. Los de Compose están en la lista.
- Los departamentos del filtro están escritos en el código en vez de pedirlos a la API.
  Cambian cada muchos años y así los filtros se pintan sin conexión, pero si el museo
  reorganiza sus departamentos habría que tocarlo a mano.
- El icono de filtros va dibujado a mano: el paquete `material-icons-core` no lo trae y
  el `extended` pesa demasiado para un icono suelto.

## Siguientes pasos

Cachear los resultados de búsqueda con una caducidad corta, para que al menos las últimas
búsquedas funcionen sin conexión. Añadir recarga deslizando. Y escribir tests de interfaz,
que ahora mismo toda la comprobación de las pantallas la he hecho a mano en el móvil.
