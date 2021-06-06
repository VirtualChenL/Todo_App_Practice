package com.example.todoapp.taskdetails

import android.view.View

/**
 * 监听用户动作的借口
 */
interface TaskDetailUserActionsListener {
    fun onCompleteChanged(v: View)
}