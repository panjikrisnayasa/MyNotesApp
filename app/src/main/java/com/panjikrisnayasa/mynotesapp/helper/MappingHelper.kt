package com.panjikrisnayasa.mynotesapp.helper

import android.database.Cursor
import com.panjikrisnayasa.mynotesapp.Note
import com.panjikrisnayasa.mynotesapp.db.DatabaseContract

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor): ArrayList<Note> {
        val notesList = ArrayList<Note>()
        notesCursor.moveToFirst()
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

}