package com.example.todoapp.taskdetails

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.example.todoapp.R
import com.example.todoapp.addedittasks.AddEditTaskActivity
import com.example.todoapp.addedittasks.AddEditTaskFragment
import com.example.todoapp.taskdetails.TaskDetailFragment.Companion.REQUEST_EDIT_TASK
import com.example.todoapp.util.*

/**
 * task详情页面
 */

class TaskDetailActivity : AppCompatActivity(), TaskDetailNavigator {

    private lateinit var taskDetailViewModel: TaskDetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.taskdetail_act)

        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        replaceFragmentInActivity(findOrCreateViewFragment(), R.id.contentFrame)
        taskDetailViewModel = obtainViewModel()

        subscribeToNavigationChanges(taskDetailViewModel)


    }

    private fun findOrCreateViewFragment() =
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: TaskDetailFragment.newInstance(intent.getStringExtra(EXTRA_TASK_ID))

    /**
     * 订阅测滑菜单栏改变
     * 作用未知？
     *
     */
    private fun subscribeToNavigationChanges(taskDetailViewModel: TaskDetailViewModel) {
        val activity = this@TaskDetailActivity
        taskDetailViewModel.run {
            editTaskCommand.observe(activity, Observer { activity.onStartEditTask() })
            deleteTaskCommand.observe(activity, Observer { activity.onTaskDeleted() })
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_TASK) {
            if (resultCode == ADD_EDIT_RESULT_OK) {
                setResult(EDIT_RESULT_OK)
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    fun obtainViewModel(): TaskDetailViewModel = obtainViewModel(TaskDetailViewModel::class.java)

    override fun onTaskDeleted() {
        setResult(DELETE_RESULT_OK)
        finish()
    }

    override fun onStartEditTask() {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID)
        val intent = Intent(this, AddEditTaskActivity::class.java).apply {
            putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
        }
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    companion object {
        const val EXTRA_TASK_ID = "TASK_ID"

    }


}