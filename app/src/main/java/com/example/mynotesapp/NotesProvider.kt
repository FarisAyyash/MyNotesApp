package com.example.mynotesapp

import android.content.*
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.provider.BaseColumns
import android.text.TextUtils
import java.lang.IllegalArgumentException
import java.util.HashMap
class NotesProvider () : ContentProvider() {
    companion object {
        val PROVIDER_NAME = "com.example.MyNotesApp.NotesProvider"
        val URL = "content://" + PROVIDER_NAME + "/notes"
        val CONTENT_URI = Uri.parse(URL)
        val _ID = "_id"
        val TITLE = "title"
        val CONTENT = "content"
        private val NOTES_PROJECTION_MAP: HashMap<String, String>? = null
        val NOTES = 1
        val NOTE_ID = 2
        val uriMatcher: UriMatcher? = null
        val DATABASE_NAME = "Keep Notes"
        val NOTES_TABLE_NAME = "notes"
        val DATABASE_VERSION = 1
        val CREATE_DB_TABLE =
            " CREATE TABLE " + NOTES_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + " title TEXT NOT NULL, " +
                    " content TEXT NOT NULL);"
    }

    private var sUriMatcher = UriMatcher(UriMatcher.NO_MATCH);

    init {
        sUriMatcher.addURI(PROVIDER_NAME, "notes", NOTES);
        sUriMatcher.addURI(PROVIDER_NAME, "notes/#", NOTE_ID);
    }

    private var db: SQLiteDatabase? = null

    private class DatabaseHelper internal constructor(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_DB_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME)
            onCreate(db)
        }
    }

    override fun onCreate(): Boolean {
        val context = context
        val dbHelper = DatabaseHelper(context)
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.  */
        db = dbHelper.writableDatabase
        return if (db == null) false else true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val rowID = db!!.insert(NOTES_TABLE_NAME, "", values)
        /**
         * If record is added successfully
         */
        if (rowID > 0) {
            val _uri = ContentUris.withAppendedId(CONTENT_URI, rowID)
            context!!.contentResolver.notifyChange(_uri, null)
            return _uri
        }
        throw SQLException("Failed to add a record into $uri")
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        var sortOrder = sortOrder
        val qb = SQLiteQueryBuilder()
        qb.tables = NOTES_TABLE_NAME
        when (uriMatcher!!.match(uri)) {
            NOTE_ID -> qb.appendWhere(_ID + "=" + uri.pathSegments[1])
            else -> { null
            }
        }
        if (sortOrder == null || sortOrder == "") {
            /*** By default sort on student names*/
            sortOrder = TITLE
        }
        val c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder)
        /**
          * register to watch a content URI for changes  */
        c.setNotificationUri(context!!.contentResolver, uri)
        return c
    }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        when (uriMatcher!!.match(uri)) {
            NOTES -> count = db!!.delete(
                NOTES_TABLE_NAME, selection,
                selectionArgs
            )
            NOTE_ID -> {
                val id = uri.pathSegments[1]
                count = db!!.delete(
                    NOTES_TABLE_NAME,
                    _ID + " = " + id +
                            if (!TextUtils.isEmpty(selection)) " AND ($selection)" else "",
                    selectionArgs
                )
            }
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        when (uriMatcher!!.match(uri)) {
            NOTES -> count = db!!.update(
                NOTES_TABLE_NAME, values, selection,
                selectionArgs
            )
            NOTE_ID -> count = db!!.update(
                NOTES_TABLE_NAME,
                values,
                _ID + " = " + uri.pathSegments[1] + (if (!TextUtils.isEmpty(selection)) " AND ($selection)" else ""),
                selectionArgs
            )
            else -> throw IllegalArgumentException("Unknown URI $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return count
    }
    override fun getType(uri: Uri): String? {
        when (uriMatcher!!.match(uri)) {
            NOTES -> return "vnd.android.cursor.dir/vnd.example.students"
            NOTE_ID -> return "vnd.android.cursor.item/vnd.example.students"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }
    /*fun getNoteIdByTitle(contentResolver: ContentResolver?, noteUri: Uri, selection: String, selectionArgs: Array<String>): Long? {
        val projection = arrayOf("_id")

        val cursor: Cursor? = contentResolver?.query(noteUri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex("_id")
                return it.getLong(columnIndex)
            }
        }

        return null
    }*/
}
