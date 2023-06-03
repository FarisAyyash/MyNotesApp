package com.example.mynotesapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val addNoteButton : Button = findViewById(R.id.button)
        addNoteButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)

        }
        val shareNoteButton : Button = findViewById(R.id.button2)
        shareNoteButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Sample note content")
            startActivity(intent)
        }
    }
    /*fun onClickRetrieveNotes(view: View?) {
        // Retrieve student records
        val URL = "content://com.example.MyNotesApp.NotesProvider"
        val Notes = Uri.parse(URL)
        //\  val c = contentResolver!!.query(students,null,null,null,"name")
        var c = contentResolver.query(Notes, null, null, null,null)
        //val //c = managedQuery(students, null, null, null, "name")
        if (c != null) {
            if (c?.moveToFirst()!!) {
                do {

                                    Toast.makeText(this, c.getString(c.getColumnIndex(NotesProvider._ID)) +
            ", " + c.getString(c.getColumnIndex(NotesProvider.TITLE)) + ", "
            + c.getString(c.getColumnIndex(NotesProvider.CONTENT)), Toast.LENGTH_SHORT).show()
                } while (c.moveToNext())
            }
        }
    }
    <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/gradient"
        android:text="Share Note"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.498"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/button3" />*/
}