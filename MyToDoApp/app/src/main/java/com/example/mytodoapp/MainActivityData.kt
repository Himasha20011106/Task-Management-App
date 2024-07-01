package com.example.mytodoapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mytodoapp.database.ToDo
import com.example.mytodoapp.database.ToDoRepository

class MainActivityData : ViewModel() {
    private val _data = MutableLiveData<List<ToDo>>()
    val data: LiveData<List<ToDo>> get() = _data

    private var currentSortBy: Pair<String, Boolean> = Pair("title", true)

    fun setData(todoList: List<ToDo>) {
        _data.value = sortData(todoList)
    }

    fun setSortBy(sortBy: Pair<String, Boolean>) {
        currentSortBy = sortBy
        _data.value = _data.value?.let { sortData(it) }
    }

    private fun sortData(todoList: List<ToDo>): List<ToDo> {
        return when (currentSortBy.first) {
            "title" -> {
                if (currentSortBy.second) {
                    todoList.sortedBy { it.item }
                } else {
                    todoList.sortedByDescending { it.item }
                }
            }
            "date" -> {
                if (currentSortBy.second) {
                    todoList.sortedBy { it.date }
                } else {
                    todoList.sortedByDescending { it.date }
                }
            }
            else -> todoList
        }
    }
}