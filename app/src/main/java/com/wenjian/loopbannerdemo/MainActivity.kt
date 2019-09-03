package com.wenjian.loopbannerdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.wenjian.loopbanner.LoopAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //设置选中监听
        lb1.setOnPageSelectListener {
//            Log.d(TAG, "select = $it")
        }

        //设置是否自动轮播，默认为true
        lb1.setAutoLoop(false)

        //设置切换时长
        lb1.setTransformDuration(1000)

        //设置图片资源并添加page点击事件
        lb1.setImages(DataCenter.loadImages()) { _, position ->
            Toast.makeText(this, "position=$position", Toast.LENGTH_SHORT).show()
        }




        //仅仅设置图片资源
//        lb1.setImages(DataCenter.loadImages())
        //设置指示器风格，有3种风格可选
//        lb1.setIndicatorStyle(LoopBanner.Style.NORMAL)
//        lb1.setIndicatorStyle(LoopBanner.Style.JD)
//        lb1.setIndicatorStyle(LoopBanner.Style.PILL)

        //设置中心page的左右边距
        lb2.setLrMargin(40)
        //设置page之间的间距
        lb2.pageMargin = 10

        //开启调试模式,有关键日志输出
        lb2.openDebug()
        //允许左右缩放,默认缩放比例为0.85
        lb2.enableScale()

        //直接设置adapter,默认的itemView是ImageView
        lb2.adapter = object : LoopAdapter<String>(DataCenter.loadImages()) {
            override fun onBindView(holder: ViewHolder, data: String, position: Int) {
                val itemView = holder.itemView as ImageView
                Glide.with(holder.context)
                    .load(data)
                    .apply(
                        //实现图片圆角
                        RequestOptions()
                            .transforms(
                                CenterCrop(), RoundedCorners(20)
                            )
                    )
                    .into(itemView)
                itemView.setOnClickListener {
                    Toast.makeText(this@MainActivity, "position=$position", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //自定义布局
        lb3.adapter = MyAdapter()

    }

    override fun onStart() {
        super.onStart()
        lb3.adapter?.setNewData(DataCenter.loadEntities())
    }


    class MyAdapter : LoopAdapter<BannerEntity>(R.layout.lay_banner_item) {
        override fun onBindView(holder: ViewHolder, data: BannerEntity, position: Int) {
            val image = holder.getView<ImageView>(R.id.iv_image)

            Glide.with(holder.context)
                .load(data.url)
                .apply(RequestOptions.centerCropTransform())
                .into(image)
            holder.setText(R.id.tv_title, data.title)

            holder.itemView.setOnClickListener {
                Toast.makeText(holder.context, "position=$position", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
