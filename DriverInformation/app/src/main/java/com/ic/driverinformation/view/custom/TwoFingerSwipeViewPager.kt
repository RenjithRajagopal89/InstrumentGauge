package com.ic.driverinformation.view.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field


class TwoFingerSwipeViewPager: ViewPager {

    private var isMultiTouch = false
    private var noOfFinger = 0

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        setMyScroller();
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val action: Int? = ev?.getAction()

        if (action != null) {
            Log.i(javaClass.simpleName, "Action = " + (action and MotionEvent.ACTION_MASK))
            when(action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_POINTER_DOWN -> {
                    isMultiTouch = true
                    noOfFinger = ev.pointerCount
                    return super.dispatchTouchEvent(ev)
                }
                MotionEvent.ACTION_DOWN -> return super.dispatchTouchEvent(ev)
                MotionEvent.ACTION_MOVE -> {
                    Log.i(javaClass.simpleName, "Action = " + (action and MotionEvent.ACTION_MASK) + " $isMultiTouch")
                    return if (isMultiTouch && noOfFinger == 2)  {
                        super.dispatchTouchEvent(ev)
                    } else false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                    isMultiTouch = false
                    noOfFinger = 0
                    return super.dispatchTouchEvent(ev)
                }
            }
        }
        return true
    }

    //down one is added for smooth scrolling
    private fun setMyScroller() {
        try {
            val viewpager: Class<*> = ViewPager::class.java
            val scroller: Field = viewpager.getDeclaredField("mScroller")
            scroller.setAccessible(true)
            scroller.set(this, MyScroller(context))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class MyScroller(context: Context?) :
        Scroller(context, DecelerateInterpolator()) {
        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/)
        }
    }
}