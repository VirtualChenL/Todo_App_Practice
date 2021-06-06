package com.example.todoapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @JvmOverloads 使用该注解，会暴露多个重载方法
 */


@Entity(tableName = "tasks")
data class Task @JvmOverloads constructor(
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "description") var description: String = "",
    @PrimaryKey @ColumnInfo(name = "vid") var id: String = UUID.randomUUID().toString()

) {
    @ColumnInfo(name = "completed")
    var isCompleted = false //是否已经完成

    //获取task标题
    val titleForList: String
        get() = if (title.isNotEmpty()) title else
            description

    //判断task是否活跃
    val isActive get() = !isCompleted

    //判断task是否为空
    val isEmpty get() = title.isEmpty() && description.isEmpty()

}