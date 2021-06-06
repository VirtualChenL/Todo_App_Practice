package com.example.todoapp.util


/**
 * 未知
 */

object EspressoIdlingResource {
    private val PESOURCE="GLOBAL"

    @JvmField val countingIdlingResource=SimpleCountingIdlingResource(PESOURCE)

    fun increment(){
        countingIdlingResource.increment()
    }

    fun decrement(){
        countingIdlingResource.decrement()
    }


}