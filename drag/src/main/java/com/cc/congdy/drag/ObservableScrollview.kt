package com.cc.congdy.drag

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView


/**
 * 请描述使用该类使用方法！！！
 *
 * @author 陈聪 2017-12-08 14:55
 */
class ObservableScrollView : ScrollView {
    private var onScollChangedListener: OnScollChangedListener? = null
    private var onScollOverListener: OnScollOverListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet,
                defStyle: Int) : super(context, attrs, defStyle)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setOnScollChangedListener(onScollChangedListener: OnScollChangedListener) {
        this.onScollChangedListener = onScollChangedListener
    }

    fun setOnScollOveredListener(onScollOverListener: OnScollOverListener) {
        this.onScollOverListener = onScollOverListener
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        if (scrollY == 0) {
            onScollOverListener!!.onScrollOver(this, clampedY, false)
        } else {
            onScollOverListener!!.onScrollOver(this, false, clampedY)
        }
    }

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        if (onScollChangedListener != null) {
            onScollChangedListener!!.onScrollChanged(this, x, y, oldx, oldy)
        }
    }

    interface OnScollChangedListener {

        fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int)

    }

    interface OnScollOverListener {

        fun onScrollOver(scrollView: ObservableScrollView, isToped: Boolean, isBottom: Boolean)

    }
}