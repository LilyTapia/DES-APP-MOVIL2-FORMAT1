package cl.duoc.veterinaria.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import cl.duoc.veterinaria.data.VeterinariaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Content Provider básico para exponer datos de la veterinaria a otras aplicaciones.
 * En este ejemplo, expone el listado de mascotas en formato de texto.
 */
class VeterinariaProvider : ContentProvider() {

    // Definición de la autoridad y URIs
    companion object {
        const val AUTHORITY = "cl.duoc.veterinaria.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/mascotas")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        // Creamos un cursor en memoria (MatrixCursor) ya que no usamos SQLite directo aquí
        val cursor = MatrixCursor(arrayOf("_id", "descripcion"))

        // Obtenemos los datos del repositorio de forma síncrona para este ejemplo
        // Nota: En producción, ContentProvider debe acceder a una BD real (Room/SQLite)
        val mascotas = runBlocking { 
            VeterinariaRepository.listaMascotas.value
        }

        mascotas.forEachIndexed { index, descripcion ->
            cursor.addRow(arrayOf(index, descripcion))
        }

        return cursor
    }

    // Métodos no implementados para este alcance de "Solo Lectura"
    override fun getType(uri: Uri): String? = "vnd.android.cursor.dir/vnd.$AUTHORITY.mascotas"
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}
