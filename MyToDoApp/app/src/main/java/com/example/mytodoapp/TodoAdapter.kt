package com.example.mytodoapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodoapp.database.ToDo
import com.example.mytodoapp.database.ToDoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TodoAdapter(
    private val items: List<ToDo>,
    private val repository: ToDoRepository,
    private val viewModel: MainActivityData,
    private val updateItemClickListener: (ToDo) -> Unit
) : RecyclerView.Adapter<TodoViewHolder>() {

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
        context = parent.context
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val item = items[position]
        holder.itemTodo.text = item.item
        holder.descriptionTodo.text = item.description

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())
        holder.dateTodo.text = dateFormat.format(item.date)

        holder.ivDelete.setOnClickListener {
            val isChecked = holder.cbTodo.isChecked
            if (isChecked) {
                CoroutineScope(Dispatchers.IO).launch {
                    repository.delete(item)
                    val data = repository.getAllTodoItems()
                    withContext(Dispatchers.Main) {
                        viewModel.setData(data)
                    }
                }
            } else {
                Toast.makeText(context, "Select the item to be deleted", Toast.LENGTH_LONG).show()
            }
        }

        holder.ivEdit.setOnClickListener {
            updateItemClickListener(item)
        }
    }
}
