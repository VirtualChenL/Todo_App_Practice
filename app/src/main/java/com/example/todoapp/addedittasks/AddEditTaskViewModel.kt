package com.example.todoapp.addedittasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todoapp.Event
import com.example.todoapp.R
import com.example.todoapp.data.Task
import com.example.todoapp.data.source.TasksDataSource
import com.example.todoapp.data.source.TasksRepository
import java.lang.RuntimeException

/**
 * add/edit的视图模型
 */
class AddEditTaskViewModel(private val tasksRepository: TasksRepository) : ViewModel(),
    TasksDataSource.GetTaskCallback {

    val title = MutableLiveData<String>()

    val description = MutableLiveData<String>()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> get() = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> get() = _snackbarText

    private val _taskUpdated = MutableLiveData<Event<Unit>>()
    val taskUpdatedEvent: LiveData<Event<Unit>> get() = _taskUpdated

    private var taskId: String? = null

    private var isNewTask: Boolean = false

    private var isDataLoad: Boolean = false

    private var taskCompleted: Boolean = false

    fun start(taskId: String?) {
        //loading
        _dataLoading.value?.let { isLoading ->
            if (isLoading) return
        }
        this.taskId=taskId
        //新task
        if(taskId==null){
            isNewTask=true
            return
        }
        //已经加载
        if(isDataLoad){
            return
        }
        isNewTask=false
        _dataLoading.value=true
        //加载
        tasksRepository.getTask(taskId,this)
    }

    override fun onTaskloaded(task: Task) {
        title.value=task.title
        description.value=task.description
        taskCompleted=task.isCompleted
        _dataLoading.value=false
        isDataLoad=true
    }

    override fun onDataNotAvailable() {
        _dataLoading.value=false
    }

    /**
     * 保存当前编辑的task
     */
    internal fun saveTask(){
        val currentTitle=title.value
        val currentDescription=description.value

        //如果有一个为空
        if(currentTitle==null ||currentDescription==null){
            _snackbarText.value =  Event(R.string.empty_task_message)
            return
        }
        //两个都为空
        if(Task(currentTitle,currentDescription).isEmpty){
            _snackbarText.value=Event(R.string.empty_task_message)
            return
        }
        val currentTaskId=taskId
        if(isNewTask||currentTaskId==null){
            createTask(Task(currentTitle,currentDescription))
        }else{
            val task=Task(currentTitle,currentDescription,currentTaskId).apply {
                isCompleted=taskCompleted
            }
            updateTask(task)
        }

    }

    private fun updateTask(task: Task) {
        if(isNewTask){
            throw RuntimeException("updateTask() was called but task is new ")
        }
        tasksRepository.saveTask(task)
        _taskUpdated.value= Event(Unit)
    }

    private fun createTask(task: Task) {
        tasksRepository.saveTask(task)
        _taskUpdated.value=Event(Unit)
    }
}