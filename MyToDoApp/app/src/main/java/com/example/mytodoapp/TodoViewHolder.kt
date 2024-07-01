package com.example.mytodoapp

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cbTodo: CheckBox = view.findViewById(R.id.cbTodo)
    val itemTodo: TextView = view.findViewById(R.id.itemTodo)
    val descriptionTodo: TextView = view.findViewById(R.id.descriptionTodo)
    val dateTodo: TextView = view.findViewById(R.id.dateTodo)
    val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    val ivEdit: ImageView = view.findViewById(R.id.ivEdit)
}