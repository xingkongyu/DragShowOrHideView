package com.cc.congdy.dragshoworhideview

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var gridview = findViewById<GridView>(R.id.grid_view)
        gridview.adapter = getBaseAdapter()
        gridview.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 -> }
    }

    private fun getBaseAdapter(): BaseAdapter? {
        return object : BaseAdapter(){
            @SuppressLint("ResourceAsColor")
            override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
                var img = ImageView(this@MainActivity)
                img.setBackgroundColor(R.color.colorAccent)
                img.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,400)
                return img
            }

            override fun getItem(p0: Int): Any {
                return 0
            }

            override fun getItemId(p0: Int): Long {
                return 0
            }

            override fun getCount(): Int {
                return 40
            }
        }

    }

}
