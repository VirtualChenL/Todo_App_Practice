package com.example.todoapp.data.source

import android.util.Log
import com.example.todoapp.data.Task
import com.example.todoapp.util.EspressoIdlingResource

//ex
/**
 * 定义数据源操作
 */
class TasksRepository(
    val taskRemoteDataSource: TasksDataSource,
    val taskLocalDataSource: TasksDataSource
) : TasksDataSource {

    var cacheTasks: HashMap<String, Task> = LinkedHashMap()

    //标记数据，强制更新
    var cacheIsDirty = false

    override fun getTasks(callback: TasksDataSource.LoadTaskCallback) {

        if (cacheTasks.isNotEmpty() && !cacheIsDirty) {
            callback.onTasksLoaded(ArrayList(cacheTasks.values))
            return
        }
        EspressoIdlingResource.increment() //将当前线程设置为繁忙
        /**
         * 判断当前cache是否有脏数据，有的话更新数据
         * 没有脏数据的情况下，首先尝试从本地加载数据，然后尝试从网络加载
         */
        if (cacheIsDirty) {
            getTaskFromRemoteDataSource(callback)
        } else {
            taskLocalDataSource.getTasks(object : TasksDataSource.LoadTaskCallback {
                override fun onTasksLoaded(tasks: List<Task>) {
                    refreshCache(tasks)
                    EspressoIdlingResource.decrement()
                    callback.onTasksLoaded(ArrayList(cacheTasks.values))
                }

                override fun onDataNotAvailable() {
                    getTaskFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val taskInCache = getTaskWithid(taskId)
        if (taskInCache != null) {
            callback.onTaskloaded(taskInCache)
            return
        }
        EspressoIdlingResource.increment()

        taskLocalDataSource.getTask(taskId, object : TasksDataSource.GetTaskCallback {
            override fun onTaskloaded(task: Task) {
                cacheAndPerform(task) {
                    EspressoIdlingResource.decrement()
                    callback.onTaskloaded(it)
                }
            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                callback.onDataNotAvailable()
            }
        })

    }


    /**
     * 保存数据至本地和远程网络
     */
    override fun saveTask(task: Task) {
        cacheAndPerform(task) {
            taskLocalDataSource.saveTask(it)
            taskRemoteDataSource.saveTask(it)
        }
    }


    override fun completeTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = true
            taskRemoteDataSource.completeTask(it)
            taskLocalDataSource.completeTask(it)
        }
    }

    override fun completeTask(taskId: String) {
        getTaskWithid(taskId)?.let {
            completeTask(it)
        }
    }


    override fun activateTask(task: Task) {
        cacheAndPerform(task) {
            it.isCompleted = false
            taskLocalDataSource.activateTask(it)
            taskRemoteDataSource.activateTask(it)
        }
    }

    override fun activateTask(taskId: String) {
        getTaskWithid(taskId)?.let {
            activateTask(it)
        }
    }

    /**
     * 清楚已完成的任务
     */
    override fun clearCompletedTasks() {
        taskLocalDataSource.clearCompletedTasks()
        taskRemoteDataSource.clearCompletedTasks()
        //过滤掉completed的task
        cacheTasks = cacheTasks.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    /**
     * 更新task
     */
    override fun refreshTasks() {
        cacheIsDirty = true
    }

    /**
     * 删除所有task
     */
    override fun deleteAllTasks() {
        taskLocalDataSource.deleteAllTasks()
        taskRemoteDataSource.deleteAllTasks()
        cacheTasks.clear()

    }

    /**
     * 根据id删除task
     */
    override fun deleteTask(taskId: String) {
        taskRemoteDataSource.deleteTask(taskId)
        taskLocalDataSource.deleteTask(taskId)
        cacheTasks.remove(taskId)
    }

    /**
     * 清空缓存
     */

    private fun refreshCache(tasks: List<Task>) {
        cacheTasks.clear()
        tasks.forEach {
            cacheAndPerform(it) {}
        }
        cacheIsDirty = false
    }



    /**
     * 刷新缓存
     */
    private inline fun cacheAndPerform(task: Task, perform: (Task) -> Unit) {
        val cacheTask = Task(task.title, task.description, task.id).apply {
            isCompleted = task.isCompleted
        }
        cacheTasks.put(cacheTask.id, cacheTask)
        perform(cacheTask)
    }

    /**
     * 根据id获取task
     */
    private fun getTaskWithid(Id: String) = cacheTasks[Id]

    /**
     * 从远程网络获得数据
     */
    private fun getTaskFromRemoteDataSource(taskCallback: TasksDataSource.LoadTaskCallback) {
        taskRemoteDataSource.getTasks(object : TasksDataSource.LoadTaskCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                refreshCache(tasks)
                refreshLocalDataSource(tasks)

                EspressoIdlingResource.decrement()
                taskCallback.onTasksLoaded(ArrayList(cacheTasks.values))

            }

            override fun onDataNotAvailable() {
                EspressoIdlingResource.decrement()
                taskCallback.onDataNotAvailable()
            }
        })


    }

    /**
     * 刷新本地数据
     */
    private fun refreshLocalDataSource(tasks: List<Task>) {
        taskLocalDataSource.deleteAllTasks()
        for (task in tasks) {
            taskLocalDataSource.saveTask(task)
        }
    }

    //单例模式
    companion object {
        private var INSTANCE: TasksRepository? = null

        @JvmStatic
        fun getInstance(
            tasksRemoteDataSource: TasksDataSource,
            taskLocalDataSource: TasksDataSource
        ) =
            INSTANCE ?: synchronized(TasksRepository::class.java) {
                INSTANCE ?: TasksRepository(
                    tasksRemoteDataSource,
                    taskLocalDataSource
                ).also { INSTANCE = it }
            }
        @JvmStatic
        fun destoryInstance(){
            INSTANCE=null
        }
    }
}

