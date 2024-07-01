package com.example.mytodoapp

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodoapp.database.ToDo
import com.example.mytodoapp.database.ToDoDatabase
import com.example.mytodoapp.database.ToDoRepository
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: TodoAdapter
    private lateinit var viewModel: MainActivityData
    private lateinit var searchView: SearchView
    private lateinit var repository: ToDoRepository

    private var isList = true // Default is linear layout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = ToDoRepository(ToDoDatabase.getInstance(this))
        val recyclerView: RecyclerView = findViewById(R.id.rvTodoList)
        viewModel = ViewModelProvider(this)[MainActivityData::class.java]

        viewModel.data.observe(this) {
            adapter = TodoAdapter(it, repository, viewModel) { todo ->
                displayUpdateDialog(todo)
            }
            recyclerView.adapter = adapter
            recyclerView.layoutManager = if (isList) {
                LinearLayoutManager(this)
            } else {
                GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.getAllTodoItems()
            withContext(Dispatchers.Main) {
                viewModel.setData(data)
            }
        }

        val btnAddItem: Button = findViewById(R.id.btnAddTodo)
        val btnSortItem: ImageView = findViewById(R.id.sortImg)
        val btnGridItem: ImageView = findViewById(R.id.listOrGridImg)

        btnAddItem.setOnClickListener {
            displayAlert(repository)
        }

        btnSortItem.setOnClickListener {
            sortItems()
        }

        btnGridItem.setOnClickListener {
            toggleLayoutManager(recyclerView, btnGridItem)
        }

        searchView = findViewById(R.id.searchView)
        searchView.queryHint = "Enter search title"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchItems(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchItems(it) }
                return true
            }
        })
    }

    private fun displayAlert(repository: ToDoRepository) {
        val builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_custom, null)
        builder.setView(dialogView)

        val alertDialog = builder.create()

        val editTaskTitle: EditText = dialogView.findViewById(R.id.edit_task_title)
        val editTaskDescription: EditText = dialogView.findViewById(R.id.edit_task_description)
        val saveTaskButton: Button = dialogView.findViewById(R.id.save_task_button)
        val closeDialog: ImageView = dialogView.findViewById(R.id.close_dialog)

        val titleInputLayout = dialogView.findViewById<TextInputLayout>(R.id.title_input_layout)
        val descriptionInputLayout = dialogView.findViewById<TextInputLayout>(R.id.description_input_layout)

        closeDialog.setOnClickListener {
            alertDialog.dismiss()
        }


        saveTaskButton.setOnClickListener {
            val item = editTaskTitle.text.toString()
            val description = editTaskDescription.text.toString()

            // Validate input fields
            if (item.isEmpty()) {
                titleInputLayout.error = "Title is required"
                return@setOnClickListener
            } else {
                titleInputLayout.error = null
            }

            if (description.isEmpty()) {
                descriptionInputLayout.error = "Description is required"
                return@setOnClickListener
            } else {
                descriptionInputLayout.error = null
            }

            // Add new todo
            val newTodo = ToDo(item, description, Date())

            CoroutineScope(Dispatchers.IO).launch {
                repository.insert(newTodo)
                val data = repository.getAllTodoItems()
                withContext(Dispatchers.Main) {
                    viewModel.setData(data)
                }
            }

            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun displayUpdateDialog(todo: ToDo) {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_edit_custom, null)
        builder.setView(dialogView)

        val alertDialogBox = builder.create()

        val editTaskTitle: EditText = dialogView.findViewById(R.id.edit_task_title)
        val editTaskDescription: EditText = dialogView.findViewById(R.id.edit_task_description)
        val saveTaskButton: Button = dialogView.findViewById(R.id.save_task_button)
        val closeDialog: ImageView = dialogView.findViewById(R.id.close_dialog)

        val titleInputLayout = dialogView.findViewById<TextInputLayout>(R.id.title_input_layout)
        val descriptionInputLayout = dialogView.findViewById<TextInputLayout>(R.id.description_input_layout)

        editTaskTitle.setText(todo.item)
        editTaskDescription.setText(todo.description)

        closeDialog.setOnClickListener {
            alertDialogBox.dismiss()
        }

        saveTaskButton.setOnClickListener {
            val updatedTitle = editTaskTitle.text.toString().trim()
            val updatedDescription = editTaskDescription.text.toString().trim()

            // Validate input fields
            if (updatedTitle.isEmpty()) {
                titleInputLayout.error = "Title is required"
                return@setOnClickListener
            } else {
                titleInputLayout.error = null
            }

            if (updatedDescription.isEmpty()) {
                descriptionInputLayout.error = "Description is required"
                return@setOnClickListener
            } else {
                descriptionInputLayout.error = null
            }

            // Update existing todo
            todo.item = updatedTitle
            todo.description = updatedDescription

            CoroutineScope(Dispatchers.IO).launch {
                repository.update(todo)
                val data = repository.getAllTodoItems()
                withContext(Dispatchers.Main) {
                    viewModel.setData(data)
                }
            }

            alertDialogBox.dismiss()
        }

        alertDialogBox.show()
    }


    private fun sortItems() {
        var checkedItem = 0
        val items = arrayOf("Title Ascending", "Title Descending", "Date Ascending", "Date Descending")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sort By")
        builder.setPositiveButton("OK") { _, _ ->
            when (checkedItem) {
                0 -> {
                    viewModel.setSortBy(Pair("title", true))
                }
                1 -> {
                    viewModel.setSortBy(Pair("title", false))
                }
                2 -> {
                    viewModel.setSortBy(Pair("date", true))
                }
                else -> {
                    viewModel.setSortBy(Pair("date", false))
                }
            }
            // Reapply the current layout manager after sorting
            val recyclerView: RecyclerView = findViewById(R.id.rvTodoList)
            recyclerView.layoutManager = if (isList) {
                LinearLayoutManager(this)
            } else {
                GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            }
        }
            .setSingleChoiceItems(items, checkedItem) { _, selectedItemIndex ->
                checkedItem = selectedItemIndex
            }
            .setCancelable(false)
            .show()
    }

    private fun toggleLayoutManager(recyclerView: RecyclerView, btnGridItem: ImageView) {
        if (isList) {
            recyclerView.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
            btnGridItem.setImageResource(R.drawable.baseline_view_list_24)
            isList = false
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            btnGridItem.setImageResource(R.drawable.baseline_view_module_24)
            isList = true
        }
    }

    private fun searchItems(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val data = repository.searchTodoItems(query)
            withContext(Dispatchers.Main) {
                viewModel.setData(data)
            }
        }
    }
}
