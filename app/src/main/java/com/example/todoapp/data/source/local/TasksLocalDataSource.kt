package com.example.todoapp.data.source.local

import androidx.annotation.VisibleForTesting
import com.example.todoapp.data.Task
import com.example.todoapp.data.source.TasksDataSource
import com.example.todoapp.util.AppExecutors

/**
 *
 */
class TasksLocalDataSource private constructor(
    val appExecutors: AppExecutors,
    val tasksDao: TasksDao
) : TasksDataSource {


    override fun getTasks(callback: TasksDataSource.LoadTaskCallback) {
        appExecutors.diskIO.execute({
            val tasks = tasksDao.getTasks()
            appExecutors.mainThread.execute {
                if (tasks.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTasksLoaded(tasks)
                }
            }
        })
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        appExecutors.diskIO.execute({
            val task = tasksDao.getTaskById(taskId)
            appExecutors.mainThread.execute {
                if (task != null) {
                    callback.onTaskloaded(task)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        })
    }

    override fun saveTask(task: Task) {
        appExecutors.diskIO.execute { tasksDao.insertTask(task) }
    }

    override fun completeTask(task: Task) {
        appExecutors.diskIO.execute { tasksDao.updateCompleted(task.id, true) }
    }

    override fun completeTask(taskId: String) {

    }

    override fun activateTask(task: Task) {
        appExecutors.diskIO.execute {
            tasksDao.updateCompleted(task.id, false)
        }
    }

    override fun activateTask(taskId: String) {

    }

    override fun clearCompletedTasks() {
        appExecutors.diskIO.execute {
            tasksDao.deleteCompletedTasks()
        }
    }

    override fun refreshTasks() {

    }

    override fun deleteAllTasks() {
        appExecutors.diskIO.execute {
            tasksDao.deleteTasks()
        }
    }

    override fun deleteTask(taskId: String) {
        appExecutors.diskIO.execute {
            tasksDao.deleteTaskById(taskId)
        }
    }

    companion object {
        private var INSTANCE: TasksLocalDataSource? = null

        @JvmStatic
        fun getInstance(appExecutors: AppExecutors, tasksDao: TasksDao): TasksLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TasksLocalDataSource::class.java) {
                    INSTANCE = TasksLocalDataSource(appExecutors, tasksDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}