package com.example.todoapp

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


/**
 * /**
 * SwipeRefreshLayout 下拉属性控件，能触发下拉刷新。
 * 但当它包含多个child view的时候，只会通过最上面的view来处理滑动事件，
 * 此时可以通过自定义一个view继承SwipeRefreshLayout，然后重写canChildScrollUp方法来解决
*/
 */
class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {
    var scrollUpChild: View? = null

    override fun canChildScrollUp() =
        scrollUpChild?.canScrollVertically(-1) ?: super.canChildScrollUp()

}