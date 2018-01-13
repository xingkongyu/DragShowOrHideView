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
![Aaron Swartz]()










作者信息：匆匆那年
QQ：617909447
