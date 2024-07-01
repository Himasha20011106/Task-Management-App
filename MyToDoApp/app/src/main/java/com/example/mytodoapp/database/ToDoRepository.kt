package com.example.mytodoapp.database

class ToDoRepository(
    private val db:ToDoDatabase
) {

    suspend fun insert(todo:ToDo) = db.getTodoDao().insert(todo)
    suspend fun update(todo:ToDo) = db.getTodoDao().update(todo)
    suspend fun delete(todo:ToDo) = db.getTodoDao().delete(todo)
    fun searchTodoItems(query: String): List<ToDo> {
        return db.getTodoDao().searchTodoItems(query)
    }

    fun getAllTodoItems():List<ToDo> = db.getTodoDao().getAllTodoItems()
}