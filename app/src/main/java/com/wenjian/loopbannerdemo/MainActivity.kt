package com.wenjian.loopbannerdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.wenjian.loopbanner.LoopAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lb1.setOnPageSelectListener {
            Log.d(TAG, "select = $it")
        }
        //设置图片资源并添加page点击事件
        lb1.setImages(DataCenter.loadImages()) { _, position ->
            Toast.makeText(this, "position=$position", Toast.LENGTH_SHORT).show()
        }

        //直接设置adapter,默认的itemView是ImageView
        lb2.adapter = object : LoopAdapter<String>(DataCenter.loadImages()) {
            override fun onBindView(holder: ViewHolder, data: String, position: Int) {
                val itemView = holder.itemView as ImageView
                Glide.with(holder.context).load(data).into(itemView)
                itemView.setOnClickListener {
                    Toast.makeText(this@MainActivity, "position=$position", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //自定义布局
        lb3.adapter = MyAdapter(DataCenter.loadEntities())

    }


    class MyAdapter(data: List<BannerEntity>) : LoopAdapter<BannerEntity>(data, R.layout.lay_banner_item) {
        override fun onBindView(holder: ViewHolder, data: BannerEntity, position: Int) {
            val image = holder.getView<ImageView>(R.id.iv_image)
            Glide.with(holder.context).load(data.url).into(image)
            holder.setText(R.id.tv_title,data.title)

            holder.itemView.setOnClickListener {
                Toast.makeText(holder.context, "position=$position", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
