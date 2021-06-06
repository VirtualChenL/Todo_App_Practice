package com.example.todoapp.taskdetails

/**
 * 定义task详情页面的导航动作
 */
interface TaskDetailNavigator {
    //删除
    fun onTaskDeleted()

    //编辑
    fun onStartEditTask()

}