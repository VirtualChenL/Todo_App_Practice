package com.example.todoapp.addedittasks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.todoapp.R
import com.example.todoapp.util.ADD_EDIT_RESULT_OK
import com.example.todoapp.util.obtainViewModel
import com.example.todoapp.util.replaceFragmentInActivity
import com.example.todoapp.util.setupActionBar

/**
 * 添加或编辑界面
 */
class AddEditTaskActivity : AppCompatActivity(), AddEditTaskNavigator {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()//返回
        return true
    }

    override fun onTaskSaved() {
        setResult(ADD_EDIT_RESULT_OK)
        finish()
    }

    override fun onCreate(saveInstanceState: Bundle?) {
        super.onCreate(saveInstanceState)
        setContentView(R.layout.addtask_act)

        setupActionBar(R.id.toolbar) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        replaceFragmentInActivity(obtainViewFragment(), R.id.contentFrame)

        subscribeToNavigationChanges()
    }

    private fun subscribeToNavigationChanges() {
        obtainViewModel().taskUpdatedEvent.observe(this, Observer {
            this@AddEditTaskActivity.onTaskSaved()
        })
    }

    fun obtainViewModel(): AddEditTaskViewModel = obtainViewModel(AddEditTaskViewModel::class.java)

    /**
     * 获得当前fragment
     */
    private fun obtainViewFragment() = supportFragmentManager.findFragmentById(R.id.contentFrame)
        ?: AddEditTaskFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString(
                    AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID,
                    intent.getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)
                )
            }
        }

    companion object {
        const val REQUEST_CODE = 1
    }
}