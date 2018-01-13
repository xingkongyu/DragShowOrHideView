控件名称：DragShowOrHideView
---
此控件使用kotlin进行开发的，如有看不懂的地方请联系我。

作用：用于处理界面需要进行拖拽特效的情况，使用之后使用者可以根据手势将当前展示的界面进行拖拽，在超出一定范围之后抬起手指，界面会弹回到点开的控件位置，过程中会有缩放效果。

原理说明：
---
点击展示：点击指定条目时获取条目的宽高，用于动画展示的初始大小。

拖动显示：拖动时会根据拖动的距离对展示控件做缩放和背景透明效果，距离越大，展示控件越小，背景越透明。

恢复列表：拖动超过屏幕一半的距离或者点击返回按钮会有动画缩放恢复到之前点击位置并缩放到点击之前的大小。

相关使用方法如下：
---
 >1.你得获取屏幕的宽高（以下为举例代码）

            val dm = DisplayMetrics()
            //取得窗口属性
            windowManager.defaultDisplay.getMetrics(dm)
            //窗口的宽度
            screenWidth = dm.widthPixels
            //窗口高度
            screenHeight = dm.heightPixels

  >2.你得设置展示控件的headview，为了更好的效果，一般为了更好的展示效果使用图片展示，具体可以根据项目需求进行处理。

    dragview.addHeaderView(header)
    dragview.addHeaderView(header,scale)//scale为宽高比，设置之后宽度默认是屏幕宽度，高度根据比例设置

  >3.设置内容view，主要是内容描述，也可以不做设置

    dragview.addContentView(contentView)

  >4.添加展示控件的滑动监听，内容容器使用的是scrollview

    dragview.setScrollViewOnScrollChangedListener(object :ObservableScrollView.OnScollChangedListener{
                        override fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {
                            Log.e("xxxx","========>>>>$y")
                        }
                    })



展示效果
----
![Aaron Swartz](https://github.com/xingkongyu/DragShowOrHideView/blob/master/app/Hy6jUg.gif?raw=true)


完整使用代码如下：
---------

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







作者信息：匆匆那年
QQ：617909447
