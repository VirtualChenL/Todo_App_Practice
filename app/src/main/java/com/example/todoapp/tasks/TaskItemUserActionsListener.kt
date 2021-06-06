package com.example.todoapp.tasks

import android.view.View
import com.example.todoapp.data.Task

interface TaskItemUserActionsListener {
    //task状态改变
    fun onCompleteChanged(task: Task, v: View)
    //标记是否被选择
    fun onTaskClicked(task: Task)
}