package com.example.todoapp.data.source.local

import androidx.room.*
import com.example.todoapp.data.Task

/**
 * dao层，与数据库进行交互
 */
@Dao interface TasksDao {

    @Query("SELECT * FROM Tasks") fun getTasks(): List<Task>


    @Query("SELECT * FROM Tasks WHERE vid = :taskId") fun getTaskById(taskId: String): Task?


    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertTask(task: Task)


    @Update
    fun updateTask(task: Task): Int


    @Query("UPDATE tasks SET completed = :completed WHERE vid = :taskId")
    fun updateCompleted(taskId: String, completed: Boolean)


    @Query("DELETE FROM Tasks WHERE vid = :taskId") fun deleteTaskById(taskId: String): Int


    @Query("DELETE FROM Tasks") fun deleteTasks()


    @Query("DELETE FROM Tasks WHERE completed = 1") fun deleteCompletedTasks(): Int
}