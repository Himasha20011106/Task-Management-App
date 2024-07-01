package com.example.mytodoapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity  //(tableName = "todo_table") <- Rename the table
data class ToDo( //Table name eka
    var item:String?, //Table column 1
    var description:String?,
    val date: Date
){
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null //Table column 2
}
