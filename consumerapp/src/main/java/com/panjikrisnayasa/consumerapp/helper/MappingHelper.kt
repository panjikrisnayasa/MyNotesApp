package com.panjikrisnayasa.consumerapp.helper

import android.database.Cursor
import com.panjikrisnayasa.consumerapp.Note
import com.panjikrisnayasa.consumerapp.db.DatabaseContract

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()
        notesCursor?.moveToFirst()
        if (notesCursor != null)
            if (notesCursor.isFirst) {
                do {
                    val id =
                        notesCursor.getInt(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                    val title =
                        notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                    val description =
                        notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                    val date =
                        notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
                    notesList.add(Note(id, title, description, date))
                } while (notesCursor.moveToNext())
            }
        return notesList
    }

    fun mapCursorToObject(notesCursor: Cursor): Note {
        var note = Note()
        notesCursor.apply {
            moveToFirst()
            if (notesCursor.isFirst) {
                val id =
                    notesCursor.getInt(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val title =
                    notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val description =
                    notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                val date =
                    notesCursor.getString(notesCursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
                note = Note(id, title, description, date)
            }
        }
        return note
    }

}