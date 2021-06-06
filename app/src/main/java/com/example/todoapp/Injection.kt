package com.example.todoapp

import android.content.Context
import com.example.todoapp.data.source.TasksRepository
import com.example.todoapp.data.source.local.TasksLocalDataSource
import com.example.todoapp.data.source.local.ToDoDatabase
import com.example.todoapp.data.source.remote.TasksRemoteDataSource
import com.example.todoapp.util.AppExecutors

/**
 * 根据上下文获取当前database
 */
object Injection {
    fun provideTasksRepository(context: Context): TasksRepository {
        val database = ToDoDatabase.getInstance(context)
        return TasksRepository.getInstance(
            TasksRemoteDataSource,
            TasksLocalDataSource.getInstance(AppExecutors(), database.taskDao()))
    }
}