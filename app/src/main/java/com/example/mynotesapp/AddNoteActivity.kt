package com.example.mynotesapp

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.content.ContentResolver
import android.content.ContentUris
import android.content.CursorLoader
import android.provider.BaseColumns





class AddNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        val titleEditText : EditText = findViewById(R.id.title)
        val contentEditText : EditText = findViewById(R.id.content)
        val saveButton : Button = findViewById(R.id.saveButton)
        val updateButton : Button = findViewById(R.id.updateButton)
        val deleteButton : Button = findViewById(R.id.deleteButton)

        saveButton.setOnClickListener {
            val noteTitle = titleEditText.text.toString()
            val noteContent = contentEditText.text.toString()

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                saveNoteToDatabase(noteTitle, noteContent)
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                startService()
                finish()
            } else {
                Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            val noteTitle = titleEditText.text.toString()
            val noteContent = contentEditText.text.toString()

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                deleteNoteFromDatabase(noteTitle, noteContent)
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                //startService()
                finish()
            } else {
                Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
            }
        }

        updateButton.setOnClickListener {
            val noteTitle = titleEditText.text.toString()
            val noteContent = contentEditText.text.toString()

            if (noteTitle.isNotEmpty() && noteContent.isNotEmpty()) {
                updateNoteToDatabase(noteTitle, noteContent)
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                //startService()
                finish()
            } else {
                Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNoteToDatabase(title: String, content: String) {
        val values = ContentValues()
        values.put(
            NotesProvider.TITLE,title
        )
        values.put(
            NotesProvider.CONTENT,content
        )
        val uri = contentResolver.insert(
            NotesProvider.CONTENT_URI, values
        )
        Toast.makeText(baseContext, uri.toString(), Toast.LENGTH_LONG).show()
    }
    private fun updateNoteToDatabase(title: String, content: String) {
        val URL = "content://com.example.MyNotesApp.NotesProvider"
        val notes = Uri.parse(URL)
        val selection = "title = ?"
        val selectionArgs = arrayOf(title)
        val values = ContentValues()
        values.put(
            NotesProvider.TITLE,title
        )
        values.put(
            NotesProvider.CONTENT,content
        )
        val uri = contentResolver.update(
            notes, values,selection,selectionArgs
        )
        Toast.makeText(baseContext, uri.toString(), Toast.LENGTH_LONG).show()
    }
    private fun deleteNoteFromDatabase(title: String, content: String) {
       // val values = ContentValues()
        val URL = "content://com.example.MyNotesApp.NotesProvider"
        val notes = Uri.parse(URL)
        val selection = "title = ?"
        val selectionArgs = arrayOf(title)
        val noteId = getNoteIdByTitle(notes,selection,selectionArgs)
        //values.put(NotesProvider._ID,noteId)

        if (noteId != null) {
            val deleteSelection = "_id = ?"
            val deleteSelectionArgs = arrayOf(noteId.toString())
            val uri =  contentResolver.delete(notes, deleteSelection, deleteSelectionArgs)
            Toast.makeText(baseContext, uri.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, NoteService::class.java)
        serviceIntent.putExtra("inputExtra", "A note has been added")
        ContextCompat.startForegroundService(this, serviceIntent)
    }
    /*private fun getNoteIdByTitle(noteUri: Uri, selection: String, selectionArgs: Array<String>): Long? {
        val projection = arrayOf("_id")
        var noteId: Long? = null
        var c: Cursor? = contentResolver?.query(noteUri, projection, selection, selectionArgs, null)
        c?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(BaseColumns._ID)
                noteId = it.getLong(columnIndex)
            }
        }
        return noteId
    }*/
    fun getNoteIdByTitle(noteUri: Uri, selection: String, selectionArgs: Array<String>): Long? {
        val projection = arrayOf("_id")

        val cursor= contentResolver.query(noteUri, projection, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(NotesProvider._ID)
                return cursor.getLong(columnIndex)
            }
        }

        return null
    }
}