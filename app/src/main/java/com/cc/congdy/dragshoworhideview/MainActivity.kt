package com.cc.congdy.dragshoworhideview

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cc.congdy.drag.DragShowOrHideView
import kotlinx.android.synthetic.main.activity_main.*
import android.util.DisplayMetrics
import android.util.Log
import com.cc.congdy.drag.ObservableScrollView


class MainActivity : AppCompatActivity() {
    //窗口的宽度
    var screenWidth:Int = 0
    //窗口高度
    var screenHeight:Int = 0
    var pics2:List<Int>  = listOf(R.drawable.aaa,R.drawable.bbb,R.drawable.ccc,R.drawable.ddd)


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var gridview: GridView = findViewById(R.id.grid_view)
        gridview.adapter = MyAdapter(this)
        var header = ImageView(this)
        var content = TextView(this)
        gridview.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
            run {
                header.setImageResource(pics2[p2%4])
                dragview.showfull(DragShowOrHideView.ScreenLocationModel(p1.width, p1.height, (p1.left + p1.width / 2).toFloat(), (p1.top + p1.height / 2).toFloat()))
            }
        }
        val dm = DisplayMetrics()
        //取得窗口属性
        windowManager.defaultDisplay.getMetrics(dm)
        //窗口的宽度
        screenWidth = dm.widthPixels
        //窗口高度
        screenHeight = dm.heightPixels
        header.layoutParams = ViewGroup.LayoutParams(screenWidth, screenHeight)
        content.layoutParams = ViewGroup.LayoutParams(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        header.scaleType = ImageView.ScaleType.CENTER_CROP
        content.setBackgroundColor(resources.getColor(R.color.material_blue_grey_800))
        content.textSize = 40f
        content.setTextColor(resources.getColor(R.color.abc_btn_colored_borderless_text_material))
        content.text = "this is the dragshoworhideview content !!\n" +
                "this is the dragshoworhideview content !!\n" +
                "this is the dragshoworhideview content !!\n" +
                "this is the dragshoworhideview content !!\n" +
                "this is the dragshoworhideview content !!\n" +
                "this is the dragshoworhideview content !!\n" +
                "this is the dragshoworhideview content !!" +
                "\nthis is the dragshoworhideview content !!\n"
        dragview.addHeaderView(header)
                .addContentView(content)
                .setScrollViewOnScrollChangedListener(object :ObservableScrollView.OnScollChangedListener{
                    override fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {
                        Log.e("xxxx","========>>>>$y")
                    }
                })
    }

    override fun onBackPressed() {
        if (dragview.visibility == View.VISIBLE) {
            dragview.backOut()
        } else {
            super.onBackPressed()
        }
    }

    class MyAdapter(var context: Context?) : BaseAdapter() {
        var pics:List<Int>  = listOf(R.drawable.aaa,R.drawable.bbb,R.drawable.ccc,R.drawable.ddd)

        @SuppressLint("ResourceAsColor")
        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var img:ImageView? = null
            if(p1==null){
                img = ImageView(context)
                img.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 520)
                img.scaleType = ImageView.ScaleType.CENTER_CROP
            }else{
                img = p1 as ImageView
            }
            img.setImageResource(pics[p0%4])
            return img
        }

        override fun getItem(p0: Int): Any {
            return 0
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return 10
        }

    }

}
