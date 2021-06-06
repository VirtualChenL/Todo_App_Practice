package com.example.todoapp.tasks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.example.todoapp.Event
import com.example.todoapp.R
import com.example.todoapp.addedittasks.AddEditTaskActivity
import com.example.todoapp.statistics.StatisticsActivity
import com.example.todoapp.taskdetails.TaskDetailActivity
import com.example.todoapp.util.obtainViewModel
import com.example.todoapp.util.replaceFragmentInActivity
import com.example.todoapp.util.setupActionBar
import com.google.android.material.navigation.NavigationView

/**
 * 主activity
 */
class TasksActivity : AppCompatActivity(), TaskItemNavigator, TaskNavigator {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var viewModel: TasksViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_act)

        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }
        setupNavigationDrawer()
        setupViewFragment()

        viewModel = obtainViewModel().apply {
            //打开task详情
            openTaskEvent.observe(this@TasksActivity, Observer<Event<String>> { event ->
                event.getContentIfNotHandled()?.let {
                    openTaskDetails(it)
                }
            })
            //新增task
            newTaskEvent.observe(this@TasksActivity, Observer<Event<Unit>> { event ->
                event.getContentIfNotHandled()?.let {
                    this@TasksActivity.addNewTask()
                }
            })
        }
    }

    fun obtainViewModel(): TasksViewModel = obtainViewModel(TasksViewModel::class.java)

    /**
     *设置侧滑菜单栏
     */
    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setupDrawerContent(findViewById(R.id.nav_view))
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    //已经在task-list页面
                }
                R.id.statistics_navigation_menu_item -> {
                    val intent = Intent(this@TasksActivity, StatisticsActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    startActivity(intent)
                }
            }
            //关闭测滑菜单栏
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

    }

    /**
     *
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleActivityResult(requestCode, resultCode)
    }

   override fun openTaskDetails(taskId: String) {
        val intent = Intent(this, TaskDetailActivity::class.java).apply {
            putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        }
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE)
    }

    override fun addNewTask() {
        val intent = Intent(this, AddEditTaskActivity::class.java)
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_CODE)
    }


    /**
     * 设置fragment
     */
    private fun setupViewFragment() {
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: replaceFragmentInActivity(TasksFragment.newInstance(), R.id.contentFrame)
    }

}