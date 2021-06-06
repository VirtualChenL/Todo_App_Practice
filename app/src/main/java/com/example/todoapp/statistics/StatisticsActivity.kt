package com.example.todoapp.statistics

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.todoapp.R
import com.example.todoapp.tasks.TasksActivity
import com.example.todoapp.util.obtainViewModel
import com.example.todoapp.util.replaceFragmentInActivity
import com.example.todoapp.util.setupActionBar
import com.google.android.material.navigation.NavigationView

/**
 * task统计数据显示
 */
class StatisticsActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)//从左边滑出菜单栏
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_act)

        setupActionBar(R.id.toolbar) {
            setTitle("Statictics")
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }
        setupNavigationDrawer()
        findOrCreateViewFragment()

    }

    private fun setupNavigationDrawer() {
        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setupDrawerContent(findViewById(R.id.nav_view))
    }

    /**
     * 测滑菜单
     */
    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.list_navigation_menu_item -> {
                    val intent = Intent(this@StatisticsActivity, TasksActivity::class.java)
                    startActivity(intent)
                }
                R.id.statistics_navigation_menu_item -> {
                    //
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawers()
            true
        }

    }

    private fun findOrCreateViewFragment() =
        supportFragmentManager.findFragmentById(R.id.contentFrame)
            ?: StatisticsFragment.newInstance().also {
                replaceFragmentInActivity(it, R.id.contentFrame)
            }


    fun obtainViewModel(): StatisticsViewModel = obtainViewModel(StatisticsViewModel::class.java)
}