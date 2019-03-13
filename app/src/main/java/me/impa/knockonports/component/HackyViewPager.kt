package me.impa.knockonports.component

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import java.lang.IllegalArgumentException

class HackyViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean = try {
        super.onInterceptTouchEvent(ev)
    } catch (_: IllegalArgumentException) {
        false
    }
}