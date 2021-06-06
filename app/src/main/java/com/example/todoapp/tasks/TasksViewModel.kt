package com.example.todoapp.tasks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.todoapp.Event
import com.example.todoapp.R
import com.example.todoapp.addedittasks.AddEditTaskActivity
import com.example.todoapp.data.Task
import com.example.todoapp.data.source.TasksDataSource
import com.example.todoapp.data.source.TasksRepository
import com.example.todoapp.util.ADD_EDIT_RESULT_OK
import com.example.todoapp.util.DELETE_RESULT_OK
import com.example.todoapp.util.EDIT_RESULT_OK

//ex
/**
 * tasks对应的viewmodel
 */
class TasksViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    private val _items = MutableLiveData<List<Task>>().apply {
        value = emptyList()
    }
    val items: LiveData<List<Task>> get() = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> get() = _dataLoading

    //当前taskfilter名称
    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> get() = _currentFilteringLabel

    //不存在task时的名称
    private val _noTasksLabel = MutableLiveData<Int>()
    val noTasksLabel: LiveData<Int>
        get() = _noTasksLabel

    //不存在任务时的图片
    private val _noTaskIconRes = MutableLiveData<Int>()
    val noTaskIconRes: LiveData<Int>
        get() = _noTaskIconRes

    //task可见性
    private val _tasksAddViewVisible = MutableLiveData<Boolean>()
    val tasksAddViewVisible: LiveData<Boolean>
        get() = _tasksAddViewVisible

    /**
     *
     */
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>>
        get() = _snackbarText


    //目前选择task类型
    private var _currentFiltering = TasksFilterType.ALL_TASKS

    private val isDataLoadingError = MutableLiveData<Boolean>()

    //
    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> get() = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> get() = _newTaskEvent

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    init {
        setFiltering(TasksFilterType.ALL_TASKS)
    }

    fun start() {
        loadTasks(false)
    }

    fun loadTasks(forceUpdate: Boolean) {
        loadTasks(forceUpdate, true)
    }

    /**
     * 设置当前现实task类型
     */
    fun setFiltering(requestType: TasksFilterType) {
        _currentFiltering = requestType
        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                setFilter(
                    R.string.label_all, R.string.no_tasks_all,
                    R.drawable.ic_assignment_turned_in_24dp, true
                )
            }
            TasksFilterType.ACTIVE_TASKS -> {
                setFilter(
                    R.string.label_active,
                    R.string.no_tasks_active,
                    R.drawable.ic_check_circle_24dp,
                    false
                )
            }
            TasksFilterType.COMPLETED_TASKS -> {
                setFilter(
                    R.string.label_completed,
                    R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_24dp,
                    false
                )
            }
        }
    }

    /**
     * 更换task类型
     */
    private fun setFilter(
        @StringRes filteringLabelString: Int,
        @StringRes noTasksLabelString: Int,
        @DrawableRes noTaskIconDrawable: Int,
        tasksAddVisible: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noTasksLabel.value = noTasksLabelString
        _noTaskIconRes.value = noTaskIconDrawable
        _tasksAddViewVisible.value = tasksAddVisible

    }

    /**
     * 清除所有的completed task
     */
    fun clearCompletedTasks() {
        tasksRepository.clearCompletedTasks()
        _snackbarText.value = Event(R.string.completed_tasks_cleared)
        loadTasks(false, false)
    }

    /**
     * complete或者active task
     */
    fun completeTask(task: Task, completed: Boolean) {
        if (completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    /**
     * 添加新task
     */
    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

    /**
     * 打开task详情
     */
    internal fun openTask(taskId: String) {
        _openTaskEvent.value = Event(taskId)
    }


    /**
     * 处理结果
     */
    fun handleActivityResult(requestCode: Int, resultCode: Int) {
        if (AddEditTaskActivity.REQUEST_CODE == requestCode) {
            when (resultCode) {
                EDIT_RESULT_OK -> _snackbarText.setValue(
                    Event(R.string.successfully_saved_task_message)
                )
                ADD_EDIT_RESULT_OK -> _snackbarText.setValue(
                    Event(R.string.successfully_added_task_message)
                )
                DELETE_RESULT_OK -> _snackbarText.setValue(
                    Event(R.string.successfully_deleted_task_message)
                )
            }
        }
    }

    /**
     * 显示系统信息
     */
    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    /**
     * 加载tasks，是否更新，以及现实刷新ui
     */
    private fun loadTasks(forceUpdate: Boolean, showLoadingUI: Boolean) {
        if (showLoadingUI) {
            _dataLoading.setValue(true)
        }
        if (forceUpdate) {

            tasksRepository.refreshTasks()
        }

        tasksRepository.getTasks(object : TasksDataSource.LoadTaskCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = java.util.ArrayList<Task>()

                for (task in tasks) {
                    when (_currentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                        TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                            tasksToShow.add(task)
                        }
                        TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                            tasksToShow.add(task)
                        }
                    }
                }
                if (showLoadingUI) {
                    _dataLoading.value = false
                }
                isDataLoadingError.value = false

                val itemsValue = ArrayList(tasksToShow)
                _items.value = itemsValue
            }

            override fun onDataNotAvailable() {
                isDataLoadingError.value = true
            }
        })
    }
}

