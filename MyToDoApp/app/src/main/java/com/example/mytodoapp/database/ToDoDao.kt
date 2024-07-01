package com.example.mytodoapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ToDoDao {

    @Insert
    suspend fun insert(todo: ToDo)

    @Update
    suspend fun update(todo: ToDo)

    @Delete
    suspend fun delete(todo: ToDo)

    @Query("SELECT * FROM Todo")
    fun getAllTodoItems():List<ToDo>

    @Query("SELECT * FROM Todo WHERE id=:id")
    fun getOne(id:Int):ToDo

    @Query("SELECT * FROM todo WHERE item LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTodoItems(query: String): List<ToDo>

}