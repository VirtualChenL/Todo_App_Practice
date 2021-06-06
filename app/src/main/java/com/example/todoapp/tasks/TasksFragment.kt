package com.example.todoapp.tasks

import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.todoapp.R
import com.example.todoapp.databinding.TasksFragBinding
import com.example.todoapp.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

//ex
/**
 * tasks显示主页面，
 */
class TasksFragment : Fragment() {
    private lateinit var viewDataBinding: TasksFragBinding
    private lateinit var listAdapter: TaskAdapter

    /**
     * 加载
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = TasksFragBinding.inflate(inflater, container, false).apply {
            viewmodel = (activity as TasksActivity).obtainViewModel()
        }
        setHasOptionsMenu(true)//
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start()
    }

    /**
     * 根据点击的menuitem选择动作
     */
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            //清除所有compeled的任务
            R.id.menu_clear -> {
                viewDataBinding.viewmodel?.clearCompletedTasks()
                true
            }
            R.id.menu_filter -> {
                showFilterPopupMenu()
                true
            }
            R.id.menu_refresh -> {
                viewDataBinding.viewmodel?.loadTasks(true)
                true
            }
            else -> false
        }

    /**
     * 添加菜单选项
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setupFab()
        setupListAdapter()
        setupRefreshLayout()
    }

    /**
     * 设置刷新
     */
    private fun setupRefreshLayout() {
       viewDataBinding.refreshLayout.run {
           setColorSchemeColors(
               ContextCompat.getColor(requireContext(),R.color.colorPrimary),
               ContextCompat.getColor(requireContext(),R.color.colorAccent),
               ContextCompat.getColor(requireContext(),R.color.colorPrimaryDark)
           )
           scrollUpChild=viewDataBinding.tasksList
       }
    }

    /**
     * 设置adapter
     */
    private fun setupListAdapter() {
        val viewModel =viewDataBinding.viewmodel
        if(viewModel!=null){
            listAdapter= TaskAdapter(ArrayList(0),viewModel)
            viewDataBinding.tasksList.adapter=listAdapter
        }else{
            Log.w(TAG,"ViewModel not initialized when attempting to set up adapter")
        }
    }

    /**
     * add按钮
     */
    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_task)?.let {
            it.setImageResource(R.drawable.ic_add)
            it.setOnClickListener {
                viewDataBinding.viewmodel?.addNewTask()
            }
        }
    }

    /**
     * 根据点击的itemid选择对应的task类型
     */
    private fun showFilterPopupMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewDataBinding.viewmodel?.run {
                    setFiltering(
                        when (it.itemId) {
                            R.id.active -> TasksFilterType.ACTIVE_TASKS
                            R.id.completed -> TasksFilterType.COMPLETED_TASKS
                            else -> TasksFilterType.ALL_TASKS
                        }
                    )
                    loadTasks(false)
                }
                true
            }
            show()
        }
    }
    companion object{
        fun newInstance()=TasksFragment()
        private const val TAG="TasksFragment"
    }

}
