package com.example.todoapp.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.data.Task
import com.example.todoapp.data.source.TasksDataSource
import com.example.todoapp.data.source.TasksRepository

/**
 * 数据统计，
 */

class StatisticsViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> get() = _dataLoading
    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> get() = _error

    /**
     *
     */
    private val _empty = MutableLiveData<Boolean>()
    val empty: LiveData<Boolean> get() = _empty

    private val _numOfCompletedTasks = MutableLiveData<Int>()
    val numberOfCompletedTasks: LiveData<Int> get() = _numOfCompletedTasks

    private val _numOfActiveTasks = MutableLiveData<Int>()
    val numberOfActiveTasks: LiveData<Int> get() = _numOfActiveTasks

    private var activeTasks = 0
    private var completedTasks = 0

    fun start() {
        loadStatistics()
    }

    /**
     * 加载数据
     */
    private fun loadStatistics() {
        _dataLoading.value = true

        tasksRepository.getTasks(object : TasksDataSource.LoadTaskCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                _error.value = false
                computeStates(tasks)
            }

            override fun onDataNotAvailable() {
                _error.value = true
                activeTasks = 0
                completedTasks = 0
                updateDataBindingObservables()
            }
        })

    }

    /**
     * 计算状态
     */
    private fun computeStates(tasks: List<Task>) {
        var completed = 0
        var active = 0
        for (task in tasks) {
            if (task.isCompleted) {
                completed += 1
            } else {
                active += 1
            }
        }
        activeTasks = active
        completedTasks = completed
        updateDataBindingObservables()
    }

    /**
     * 更新数据
     */
    private fun updateDataBindingObservables() {
        _numOfActiveTasks.value = activeTasks
        _numOfCompletedTasks.value = completedTasks
        _empty.value = activeTasks + completedTasks == 0
        _dataLoading.value = false
    }
}