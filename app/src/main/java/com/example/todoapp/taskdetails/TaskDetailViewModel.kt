package com.example.todoapp.taskdetails

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.todoapp.Event
import com.example.todoapp.R
import com.example.todoapp.data.Task
import com.example.todoapp.data.source.TasksDataSource
import com.example.todoapp.data.source.TasksRepository

/**
 * 监听动作?
 */
class TaskDetailViewModel(private val tasksRepository: TasksRepository) : ViewModel(),
    TasksDataSource.GetTaskCallback {

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> get() = _task

    private val _isDataAvailable = MutableLiveData<Boolean>()
    val isDataAvailable: LiveData<Boolean> get() = _isDataAvailable

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean>
        get() = _dataLoading

    private val _editTaskCommand = MutableLiveData<Event<Unit>>()
    val editTaskCommand: LiveData<Event<Unit>>
        get() = _editTaskCommand

    private val _deleteTaskCommand = MutableLiveData<Event<Unit>>()
    val deleteTaskCommand: LiveData<Event<Unit>>
        get() = _deleteTaskCommand

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText

    //
    val completed: LiveData<Boolean> = Transformations.map(_task) { input ->
        input.isCompleted
    }

    val taskId: String? get() = _task.value?.id

    fun deleteTask() {
        taskId?.let {
            tasksRepository.deleteTask(it)
            _deleteTaskCommand.value= Event(Unit)
        }
    }

    fun editTask(){
        _editTaskCommand.value= Event(Unit)
    }

    /**
     * 设置是否完成
     */
    fun setCompleted(completed:Boolean){
        val task=_task.value?:return
        if(completed){
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        }else{
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    /**
     * 启动
     */
    fun start(taskId:String?){
        if(taskId!=null){
            _dataLoading.value=true
            tasksRepository.getTask(taskId,this)
        }
    }

    /**
     * 设置task
     */
    private fun setTask(task:Task){
        this._task.value=task
        _isDataAvailable.value=task!=null
    }



    private fun showSnackbarMessage(@StringRes s: Int) {
        _snackbarText.value=Event(s)
    }

    override fun onTaskloaded(task: Task) {
       setTask(task)
        _dataLoading.value=false
    }

    override fun onDataNotAvailable() {
        _task.value=null
        _dataLoading.value=false
        _isDataAvailable.value=false
    }

    /**
     * 刷新
     */
    fun onRefresh(){
        taskId?.let {
            start(it)
        }
    }
}