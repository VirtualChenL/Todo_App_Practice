package com.example.todoapp.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import com.example.todoapp.addedittasks.AddEditTaskViewModel
import com.example.todoapp.data.Task
import com.example.todoapp.databinding.TaskItemBinding
import java.lang.IllegalStateException

/**
 *
 */
class TaskAdapter(
    private var tasks: List<Task>,
    private val tasksViewModel: TasksViewModel
) : BaseAdapter() {

    fun replaceData(tasks: List<Task>) {
        setList(tasks)
    }

    private fun setList(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: TaskItemBinding
        binding = if (convertView == null) {
            val inflater = LayoutInflater.from(parent.context)

            //
            TaskItemBinding.inflate(inflater, parent, false)
        } else {
            DataBindingUtil.getBinding(convertView) ?: throw IllegalStateException()
        }
        //
        val useraActionsListener = object : TaskItemUserActionsListener {
            override fun onCompleteChanged(task: Task, v: View) {
                val checked = (v as CheckBox).isChecked
                tasksViewModel.completeTask(task, checked)
            }

            override fun onTaskClicked(task: Task) {
                tasksViewModel.openTask(task.id)
            }
        }
        with(binding) {
            task = tasks[position]
            listener = useraActionsListener
            executePendingBindings()
        }
        return binding.root
    }

    override fun getItem(position: Int) = tasks[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = tasks.size
}