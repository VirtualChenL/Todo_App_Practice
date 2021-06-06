package com.example.todoapp.tasks

import android.widget.ListView
import androidx.databinding.BindingAdapter
import com.example.todoapp.data.Task


/**
 * taskfrag页面的 items和list的绑定
 */
object TasksListBindings {
    @BindingAdapter("app:items")
    @JvmStatic fun SetItems(listView: ListView,items:List<Task>){
        with(listView.adapter as TaskAdapter){
            replaceData(items)
        }
    }
}