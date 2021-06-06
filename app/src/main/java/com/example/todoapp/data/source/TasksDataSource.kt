package com.example.todoapp.data.source

import com.example.todoapp.data.Task

/**
 * 定义datasource的操作接口
 */
interface TasksDataSource {
    interface LoadTaskCallback {
        //加载tasks
        fun onTasksLoaded(tasks: List<Task>)

        //
        fun onDataNotAvailable()
    }

    interface GetTaskCallback {
        fun onTaskloaded(task: Task)

        fun onDataNotAvailable()
    }

    fun getTasks(callback: LoadTaskCallback)
    fun getTask(taskId: String, callback: GetTaskCallback)
    fun saveTask(task: Task)
    fun completeTask(task: Task)
    fun completeTask(taskId: String)
    fun activateTask(task: Task)
    fun activateTask(taskId: String)
    fun clearCompletedTasks()
    fun refreshTasks()
    fun deleteAllTasks()
    fun deleteTask(taskId: String)
}