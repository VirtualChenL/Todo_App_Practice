package com.example.todoapp.data.source.remote

import android.os.Handler
import com.google.common.collect.Lists
import com.example.todoapp.data.Task
import com.example.todoapp.data.source.TasksDataSource


/**
 * 模拟网络获取数据
 */
object TasksRemoteDataSource : TasksDataSource {

    private const val SERVICE_LATENCY_IN_MILLIS = 5000L

    private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>(2)

    init {
        addTask("Build tower in pisa", "Ground looks good")
        addTask("finish bridge in tochma", "found awesome grides")
    }

    private fun addTask(title: String, descreption: String) {
        val newtask = Task(title, descreption)
        TASKS_SERVICE_DATA.put(newtask.id, newtask)
    }

    override fun getTasks(callback: TasksDataSource.LoadTaskCallback) {
        //模拟网络延迟
        val tasks = Lists.newArrayList(TASKS_SERVICE_DATA.values)
        Handler().postDelayed({
            callback.onTasksLoaded(tasks)
        }, SERVICE_LATENCY_IN_MILLIS)
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val task = TASKS_SERVICE_DATA[taskId]

        //模拟网络延迟
        with(Handler()) {
            if (task != null) {
                postDelayed({ callback.onTaskloaded(task) }, SERVICE_LATENCY_IN_MILLIS)
            } else {
                postDelayed({ callback.onDataNotAvailable() }, SERVICE_LATENCY_IN_MILLIS)
            }
        }
    }

    override fun saveTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, task.id).apply {
            isCompleted = true
        }
        TASKS_SERVICE_DATA.put(task.id, completedTask)
    }

    override fun completeTask(taskId: String) {

    }

    override fun activateTask(task: Task) {
        val ativedtask = Task(task.title, task.description, task.id)
        TASKS_SERVICE_DATA.put(task.id, ativedtask)
    }

    override fun activateTask(taskId: String) {

    }

    override fun clearCompletedTasks() {
        TASKS_SERVICE_DATA =
            TASKS_SERVICE_DATA.filterValues { !it.isCompleted } as LinkedHashMap<String, Task>
    }

    override fun refreshTasks() {

    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }
}

