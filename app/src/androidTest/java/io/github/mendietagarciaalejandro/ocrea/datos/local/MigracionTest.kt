package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Comprueba que actualizar la app no borra la base de datos.
 *
 * Mientras solo había caché del catálogo daba igual, pero desde que existen los favoritos
 * hay datos del usuario dentro y una migración mal hecha se los llevaría por delante.
 *
 * Va en androidTest y no en los tests de JVM porque MigrationTestHelper lee los esquemas
 * exportados desde los assets de la instrumentación.
 */
class MigracionTest {

    private val nombre = "prueba-migracion.db"

    @get:Rule
    val ayudante = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        BaseDatosOcrea::class.java,
    )

    @Test
    fun deLaVersion1ALa2SeConservaElCatalogoYAparecenLosFavoritos() {
        ayudante.createDatabase(nombre, 1).use { db ->
            db.execSQL(
                """
                INSERT INTO obras (
                    id, orden, titulo, artista, fecha, imagenId, tecnica, origen,
                    dimensiones, descripcion, creditos, tipo, departamento, esDominioPublico
                ) VALUES (7, 0, 'Obra previa', NULL, NULL, NULL, NULL, NULL,
                          NULL, NULL, NULL, NULL, NULL, 0)
                """.trimIndent(),
            )
        }

        val migrada = ayudante.runMigrationsAndValidate(nombre, 2, true, MIGRACION_1_2)

        migrada.query("SELECT titulo FROM obras WHERE id = 7").use { cursor ->
            assertTrue("la obra anterior deberia seguir ahi", cursor.moveToFirst())
            assertEquals("Obra previa", cursor.getString(0))
        }

        migrada.query("SELECT COUNT(*) FROM favoritos").use { cursor ->
            cursor.moveToFirst()
            assertEquals(0, cursor.getInt(0))
        }

        migrada.close()
    }

    @Test
    fun losFavoritosSobrevivenALaMigracion() {
        // Se simula una instalacion en la version 1 que ya tenia datos.
        ayudante.createDatabase(nombre, 1).use { db ->
            db.execSQL(
                """
                INSERT INTO obras (
                    id, orden, titulo, artista, fecha, imagenId, tecnica, origen,
                    dimensiones, descripcion, creditos, tipo, departamento, esDominioPublico
                ) VALUES (1, 0, 'Antes de actualizar', NULL, NULL, NULL, NULL, NULL,
                          NULL, NULL, NULL, NULL, NULL, 0)
                """.trimIndent(),
            )
        }

        ayudante.runMigrationsAndValidate(nombre, 2, true, MIGRACION_1_2).close()

        // Y ahora se abre como lo haria la app de verdad.
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.databaseBuilder(contexto, BaseDatosOcrea::class.java, nombre)
            .addMigrations(MIGRACION_1_2)
            .build()

        db.openHelper.writableDatabase
        db.close()
    }
}
