package com.panjikrisnayasa.consumerapp

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.panjikrisnayasa.consumerapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.panjikrisnayasa.consumerapp.helper.MappingHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val EXTRA_STATE = "extra_state"
    }

    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Consumer Notes"

        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        rv_notes.adapter = adapter

        fab_add.setOnClickListener(this)

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                loadNotesAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadNotesAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_add -> {
                val intent = Intent(this, NoteAddUpdateActivity::class.java)
                startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when (requestCode) {
                NoteAddUpdateActivity.REQUEST_ADD -> {
                    if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                        val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)

                        if (note != null) adapter.addItem(note)
                        rv_notes.smoothScrollToPosition(adapter.itemCount - 1)

                        showSnackbarMessage("Note baru berhasil ditambahkan")
                    }
                }
                NoteAddUpdateActivity.REQUEST_UPDATE -> {
                    when (resultCode) {
                        NoteAddUpdateActivity.RESULT_UPDATE -> {
                            val note =
                                data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)

                            if (note != null) adapter.updateItem(position, note)
                            rv_notes.smoothScrollToPosition(position)

                            showSnackbarMessage("Note berhasil diubah")
                        }
                        NoteAddUpdateActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)

                            adapter.removeItem(position)

                            showSnackbarMessage("Note berhasil dihapus")
                        }
                    }
                }
            }
        }
    }

    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressbar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressbar.visibility = View.GONE
            val notes = deferredNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            } else {
                adapter.listNotes = ArrayList()
                showSnackbarMessage("Belum ada notes saat ini")
            }
        }
    }

    private fun showSnackbarMessage(message: String) {
        Snackbar.make(rv_notes, message, Snackbar.LENGTH_SHORT).show()
    }
}
