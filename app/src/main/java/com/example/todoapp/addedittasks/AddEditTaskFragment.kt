package com.example.todoapp.addedittasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.todoapp.R
import com.example.todoapp.databinding.AddtaskFragBinding
import com.example.todoapp.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * 增加task页面主要ui
 */
class AddEditTaskFragment : Fragment() {
    private lateinit var viewDataBinding: AddtaskFragBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.model?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
        setupActionBar()
        loadData()
    }

    private fun loadData() {
        viewDataBinding.model?.start(arguments?.getString(ARGUMENT_EDIT_TASK_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.addtask_frag, container, false)
        viewDataBinding = AddtaskFragBinding.bind(root).apply {
            model = (activity as AddEditTaskActivity).obtainViewModel()
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        retainInstance = false
        return viewDataBinding.root
    }

    //将悬浮按钮设置为done
    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_edit_task_done)?.let {
            it.setImageResource(R.drawable.ic_done)
            it.setOnClickListener { viewDataBinding.model?.saveTask() }
        }
    }

    /**
     * 判断是new还是edit
     */
    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.setTitle(
            if (arguments != null && arguments?.get(ARGUMENT_EDIT_TASK_ID) != null)
                "Edit TO-DO"
            else
                "New TO-DO"
        )

    }

    companion object {
        const val ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID"
        fun newInstance() = AddEditTaskFragment()
    }
}