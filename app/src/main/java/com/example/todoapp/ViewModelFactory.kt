package com.example.todoapp

/**
 * 依赖注入
 */

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.addedittasks.AddEditTaskActivity
import com.example.todoapp.addedittasks.AddEditTaskViewModel
import com.example.todoapp.data.source.TasksRepository
import com.example.todoapp.statistics.StatisticsViewModel
import com.example.todoapp.taskdetails.TaskDetailViewModel
import com.example.todoapp.tasks.TasksViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory private constructor(private val taskRepository: TasksRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = with(modelClass) {
        when {
            isAssignableFrom(StatisticsViewModel::class.java) ->
                StatisticsViewModel(taskRepository)
            isAssignableFrom(TaskDetailViewModel::class.java) ->
                TaskDetailViewModel(taskRepository)
            isAssignableFrom(AddEditTaskViewModel::class.java) ->
                AddEditTaskViewModel(taskRepository)
            isAssignableFrom(TasksViewModel::class.java) ->
                TasksViewModel(taskRepository)
            else ->
                throw IllegalArgumentException("unknown viewmodel class: ${modelClass.name}")
        }
    } as T

    companion object {
        //屏蔽android lint错误
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(application: Application) =
            INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE
                    ?: ViewModelFactory(Injection.provideTasksRepository(application.applicationContext)).also {
                        INSTANCE = it
                    }
            }

        //Guava注解，你可以把这个注解标注到类、方法或者字段上，以便你在测试的时候可以使用他们。
        @VisibleForTesting
        fun destoryInstance() {
            INSTANCE = null
        }

    }
}

