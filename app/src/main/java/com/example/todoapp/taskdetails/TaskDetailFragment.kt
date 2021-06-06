package com.example.todoapp.taskdetails


import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment

import com.example.todoapp.R
import com.example.todoapp.databinding.TaskdetailFragBinding
import com.example.todoapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

/**
 * task详情页面的主要ui
 */
class TaskDetailFragment : Fragment() {
    private lateinit var viewDataBinding: TaskdetailFragBinding
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_LONG)
        }
    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.fab_edit_task)?.setOnClickListener {
            viewDataBinding.viewmodel?.editTask()
        }
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.start(arguments?.getString(ARGUMENT_TASK_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.taskdetail_frag, container, false)
        viewDataBinding = TaskdetailFragBinding.bind(view).apply {
            viewmodel = (activity as TaskDetailActivity).obtainViewModel()
            listener = object : TaskDetailUserActionsListener {
                override fun onCompleteChanged(v: View) {
                    viewmodel?.setCompleted((v as CheckBox).isChecked)
                }
            }
        }
        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        return view
    }

    /**
     * 删除task操作
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewDataBinding.viewmodel?.deleteTask()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }


    companion object {

        const val ARGUMENT_TASK_ID = "TASK_ID"
        const val REQUEST_EDIT_TASK = 1

        fun newInstance(taskId: String) = TaskDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARGUMENT_TASK_ID, taskId)
            }
        }
    }
}