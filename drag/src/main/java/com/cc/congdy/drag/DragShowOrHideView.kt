package com.cc.congdy.drag

import android.animation.Animator
import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.animation.ObjectAnimator
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import kotlinx.android.synthetic.main.view_show_video.view.*
import java.io.Serializable


/**
 * 用于展示相关页面，且展示的页面可以拖动回复到之前状态
 *
 * @author 陈聪 2017-12-08 14:08
 */
open class DragShowOrHideView(context: Context, attributes: AttributeSet?) : FrameLayout(context, attributes) {

    constructor(context: Context) : this(context, null)

    var rectf: RectF? = null//辅助坐标计算
    val STATE_NORMAL: Int = 0//正常状态
    val STATE_VIDEO_MOVE: Int = 1//视频窗口拖动状态
    var currState: Int = STATE_NORMAL
    var param: FrameLayout.LayoutParams? = null
    var sv: ObservableScrollView
    var totalHeight: Int = 0//当前容器的总高度
    var totalwidth: Int = 0//当前容器的总宽度
    var isToped: Boolean = true
    var rootview: View
    //坐标开始位置
    var start_x: Float = 0F
    var start_y: Float = 0F
    val LENGTH_SCROLL_OUT: Float = 50F
    //滑动的绝对位置差值
    var abs_x: Float = 0F
    var abs_y: Float = 0F
    //滑动之后的位置相对坐标
    var to_x: Float = 0F
    var to_y: Float = 0F
    //手指up之后的相对坐标
    var end_x: Float = 0F
    var end_y: Float = 0F
    //view的缩放比例
    var scale: Float = 1F
    //当前返回外层控件中心点相对父布局坐标相关信息
    var screenLocationModel: ScreenLocationModel? = null
    /**默认向右滑动事件为退出展示*/
    private var scrollToRightDefault: Boolean = true
    /**滑动事件监听*/
    var onScrollLeftOrRightListener: OnScrollToLeftOrRightListener? = null
    /**拖动控件相关状态监听*/
    var onViewStateChangeListener: OnViewStateChangeBackListener? = null

    init {
        rootview = View.inflate(context, R.layout.view_show_video, this)
        sv = rootview.findViewById(R.id.sv_view)
        param = sv.layoutParams as LayoutParams?
        sv.setOnScollOveredListener(object : ObservableScrollView.OnScollOverListener {
            override fun onScrollOver(scrollView: ObservableScrollView, isToped: Boolean, isBottom: Boolean) {
                this@DragShowOrHideView.isToped = isToped
            }
        })
    }

    fun setScrollViewOnTouchListener(l: OnTouchListener){
        sv.setOnTouchListener(l)
    }

    /**
     * 添加头部不做透明变化部分
     */
    fun addHeaderView(view: View): DragShowOrHideView {
        if (view == null) throw Exception("addHeaderView ->view is null,please check !!")
        ll_header.addView(view)
        return this
    }

    /**
     * 添加头部不做透明变化部分
     * 宽度根据父布局宽度
     * 高度根据给定的比例设置
     */
    fun addHeaderViewFull(view: View, width: Int, height: Int): DragShowOrHideView {
        if (view == null) throw Exception("addHeaderView ->view is null,please check !!")
        ll_header.addView(view)
        var param = view!!.layoutParams
        param!!.width = width
        param!!.height = height
        view!!.layoutParams = param
        return this
    }

    /**
     * 添加下面做透明变化部分
     */
    fun addContentView(view: View): DragShowOrHideView {
        if (view == null) throw Exception("addContentView ->view is null,please check !!")
        ll_other.addView(view)
        return this
    }

    /**
     * 设置左滑动和右滑动事件监听
     */
    fun addScrollLeftOrRightListener(onScrollLeftOrRightListener: OnScrollToLeftOrRightListener?): DragShowOrHideView {
        this.onScrollLeftOrRightListener = onScrollLeftOrRightListener
        return this
    }

    /**
     * 设置拖动控件相关状态监听
     */
    fun addViewStateChangeListener(onViewStateChangeListener: OnViewStateChangeBackListener?): DragShowOrHideView {
        this.onViewStateChangeListener = onViewStateChangeListener
        return this
    }

    /**
     * 设置是否使用默认右滑动事件（退出展示）
     */
    fun setScrollToRightDefault(isDefault: Boolean): DragShowOrHideView {
        this.scrollToRightDefault = isDefault
        return this
    }

    /**
     * 事件拦截
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (currState == STATE_VIDEO_MOVE) {
            true
        } else {
            super.onInterceptTouchEvent(ev)
        }

    }

    /**
     * 向右滑动
     */
    private fun toRightFliding() {
        rootview.visibility = View.GONE
    }

    /**
     * 事件分发
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                start_x = ev.x
                start_y = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                //进入拖动状态
                if (isToped && ev.y > start_y && Math.abs(ev.y - start_y) > 20) {
                    if (currState == STATE_NORMAL) {
                        start_x = ev.x
                        start_y = ev.y
                    }
                    currState = STATE_VIDEO_MOVE

                }
                if (currState == STATE_VIDEO_MOVE) {
                    doDrag(ev)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (currState == STATE_NORMAL) {
                    if (Math.abs(ev.x - start_x) > LENGTH_SCROLL_OUT &&
                            (Math.abs(ev.x - start_x) / Math.abs(ev.y - start_y) > 4)) {
                        if (ev.x > start_x) {
                            if (scrollToRightDefault) {
                                onScrollLeftOrRightListener?.scrollToRight()
                            } else {
                                toRightFliding()//向右滑动事件
                            }
                        } else if (ev.x < start_x) {
                            //向左滑动
                            onScrollLeftOrRightListener?.scrollToLeft()
                        }
                    }
                } else {
                    if (((sv.top + totalHeight * (1 - scale) / 2) >= totalHeight / 2 || (sv.bottom - totalHeight * (1 - scale) / 2) <= totalHeight / 2)) {
                        // 如果拖动之后的顶部过了屏幕一半则退出页面
                        end_x = to_x
                        end_y = to_y
                        toPosition()
                    } else {//进行恢复到原本状态
                        end_x = to_x
                        end_y = to_y
                        toPositionFull()
                    }
                    currState = STATE_NORMAL
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun doDrag(event: MotionEvent) {
        onViewStateChangeListener?.viewOnDrogingStart()
        to_x = event.x - start_x
        to_y = event.y - start_y
        abs_x = Math.abs(to_x)
        abs_y = Math.abs(to_y)
        scale = (totalHeight - abs_y) / totalHeight
        //缩放处理
        sv.scaleX = scale
        sv.scaleY = scale
        //位移处理
        sv.layout((rectf!!.centerX() - totalwidth / 2 + to_x).toInt(),
                (rectf!!.centerY() - totalHeight / 2 + to_y).toInt(),
                (rectf!!.centerX() + totalwidth / 2 + to_x).toInt(),
                (rectf!!.centerY() + totalHeight / 2 + to_y).toInt())

        //非主体透明度处理
        if (abs_y > 100 && ll_other.alpha > 0.0f) {
            ll_other.alpha = if (ll_other.alpha > 0.3f) scale * scale * scale else 0f
        }
        //背景透明度处理
        if (abs_y > 40)
            view_bg.alpha = scale
    }

    /**
     * 恢复到原本状态
     * 注意：不能直接使用动画去做动画效果，不然会出现属性错位问题
     */
    private fun toPositionFull() {

        val anim = ObjectAnimator//
                .ofFloat(sv, "tran", abs_y, 0f)//
                .setDuration(300)//
        anim.start()
        anim.addUpdateListener { animation ->
            val cVal = animation.animatedValue as Float
            to_x = cVal / abs_y * end_x
            to_y = cVal / abs_y * end_y
            scale = (totalHeight - cVal) / totalHeight
            //缩放处理
            sv.scaleX = scale
            sv.scaleY = scale
            //位移处理
            sv.layout((rectf!!.centerX() - totalwidth / 2 + to_x).toInt(),
                    (rectf!!.centerY() - totalHeight / 2 + to_y).toInt(),
                    (rectf!!.centerX() + totalwidth / 2 + to_x).toInt(),
                    (rectf!!.centerY() + totalHeight / 2 + to_y).toInt())
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                view_bg.alpha = 1f
                ll_other.alpha = 1f
                onViewStateChangeListener?.viewOnDrogingEnd()
            }
        })

    }

    /**
     * 全屏-》进入位置动画
     * 注意：不能直接使用动画去做动画效果，不然会出现属性错位问题
     */
    private fun toPosition() {
        if (screenLocationModel == null) throw Exception("ScreenLocationModel can not be null,you must to call showfull() mothed !")
        //外部坐标进行换算
        var out_x: Float = screenLocationModel!!.location_x - rectf!!.centerX() - end_x
        var out_y: Float = screenLocationModel!!.location_y - rectf!!.centerY() - end_y
        var out_abs_y = Math.abs(out_y - end_y)
        var out_start_scale = scale
        var end_scale = screenLocationModel!!.width.toFloat() / totalwidth


        val anim = ObjectAnimator//
                .ofFloat(sv, "tran", 0f, out_abs_y)//
                .setDuration(300)//
        anim.start()
        anim.addUpdateListener { animation ->
            val cVal = animation.animatedValue as Float
            to_x = cVal / out_abs_y * out_x + end_x
            to_y = cVal / out_abs_y * out_y + end_y
            scale = out_start_scale + (cVal / out_abs_y) * (end_scale - out_start_scale)
            //缩放处理
            sv.scaleX = scale
            sv.scaleY = scale
            //位移处理
            sv.layout((rectf!!.centerX() - totalwidth / 2 + to_x).toInt(),
                    (rectf!!.centerY() - totalHeight / 2 + to_y).toInt(),
                    (rectf!!.centerX() + totalwidth / 2 + to_x).toInt(),
                    (rectf!!.centerY() + totalHeight / 2 + to_y).toInt())
            //透明度处理
            view_bg.alpha = 0f
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                view_bg.alpha = 0f
                ll_other.alpha = 0f
                visibility = View.GONE
                onViewStateChangeListener?.fullToPosition()
            }
        })

    }

    /**
     * 点击进入满屏展示
     * 注意：不能直接使用动画去做动画效果，不然会出现属性错位问题
     */
    private fun clickToFullScreen() {

        //外部坐标进行换算
        var out_x: Float = screenLocationModel!!.location_x - rectf!!.centerX()
        var out_y: Float = screenLocationModel!!.location_y - rectf!!.centerY()
        var out_abs_y = Math.abs(out_y)
        var out_start_scale = screenLocationModel!!.width.toFloat() / totalwidth
        var end_scale = 1f


        val anim = ObjectAnimator//
                .ofFloat(sv, "tran", out_abs_y, 0f)//
                .setDuration(300)//
        anim.start()
        anim.addUpdateListener { animation ->
            val cVal = animation.animatedValue as Float
            to_x = cVal / out_abs_y * out_x
            to_y = cVal / out_abs_y * out_y
            scale = out_start_scale + (1 - cVal / out_abs_y) * (end_scale - out_start_scale)
            //缩放处理
            sv.scaleX = scale
            sv.scaleY = scale
            //位移处理
            sv.layout((rectf!!.centerX() - totalwidth / 2 + to_x).toInt(),
                    (rectf!!.centerY() - totalHeight / 2 + to_y).toInt(),
                    (rectf!!.centerX() + totalwidth / 2 + to_x).toInt(),
                    (rectf!!.centerY() + totalHeight / 2 + to_y).toInt())
        }
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                view_bg.alpha = 1f
                ll_other.alpha = 1f
                onViewStateChangeListener?.positionToFullEnd()
            }
        })

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        totalHeight = b - t
        totalwidth = r - l
        rectf = RectF(l.toFloat(), t.toFloat(), r.toFloat(), b.toFloat())
        super.onLayout(changed, l, t, r, b)
    }

    /**
     * 从点击位置进入展示界面
     */
    fun showfull(screenLocationModel: ScreenLocationModel) {
        sv.scrollTo(0,0)
        this.screenLocationModel = screenLocationModel
        visibility = View.VISIBLE
        sv.visibility = View.VISIBLE
        if (rectf == null) {
            //增加整体布局监听
            val vto = viewTreeObserver
            vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    totalHeight = height
                    totalwidth = width
                    rectf = RectF(0f, 0f, width.toFloat(), height.toFloat())
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                    clickToFullScreen()
                }
            })
        } else {
            clickToFullScreen()
        }
    }

    /**
     * 退出展示页面
     */
    fun backOut() {
        onViewStateChangeListener?.viewOnFinishStart()
        end_x = 0f
        end_y = 0f
        toPosition()
    }

    /**
     * 向右滑动回调
     */
    interface OnScrollToLeftOrRightListener {
        fun scrollToRight()
        fun scrollToLeft()
    }

    /**
     * 控件相关状态监听
     */
    interface OnViewStateChangeBackListener {
        /**
         * 指定位置到全屏动画结束回调
         */
        fun positionToFullEnd()

        /**
         * 全屏恢复到之前位置动画结束回调
         */
        fun fullToPosition()

        /**
         * 控件被拖动之前回调
         */
        fun viewOnDrogingStart()

        /**
         * 控件被拖动恢复全屏展示动画结束之后回调
         */
        fun viewOnDrogingEnd()

        /**
         * 展示界面退出前回调
         */
        fun viewOnFinishStart()
    }

    /**
     * 初始进入坐标
     *
     * @author 陈聪 2017-12-11 12:55
     */
    class ScreenLocationModel(
            var width: Int = 0,
            var height: Int = 0,
            var location_x: Float = 0f,
            var location_y: Float = 0f
    ) : Serializable
}