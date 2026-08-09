package io.github.mendietagarciaalejandro.ocrea.datos.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Desde que existen los favoritos ya no vale borrar la base de datos al cambiar el
 * esquema: el catálogo se puede volver a bajar, pero lo que el usuario ha guardado no.
 */
val MIGRACION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favoritos (
                id INTEGER NOT NULL PRIMARY KEY,
                guardadaEn INTEGER NOT NULL,
                titulo TEXT NOT NULL,
                artista TEXT,
                fecha TEXT,
                imagenId TEXT,
                tecnica TEXT,
                origen TEXT,
                dimensiones TEXT,
                descripcion TEXT,
                creditos TEXT,
                tipo TEXT,
                departamento TEXT,
                esDominioPublico INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }
}
