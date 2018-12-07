package com.wenjian.loopbannerdemo

/**
 * Description: DataCenter
 * Date: 2018/12/7
 *
 * @author jian.wen@ubtrobot.com
 */

object DataCenter {
    fun loadImages() = arrayListOf<String>().apply {
        add("http://www.wanandroid.com/blogimgs/ab17e8f9-6b79-450b-8079-0f2287eb6f0f.png")
        add("http://www.wanandroid.com/blogimgs/fb0ea461-e00a-482b-814f-4faca5761427.png")
        add("http://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png")
        add("http://www.wanandroid.com/blogimgs/00f83f1d-3c50-439f-b705-54a49fc3d90d.jpg")
        add("http://www.wanandroid.com/blogimgs/90cf8c40-9489-4f9d-8936-02c9ebae31f0.png")
        add("http://www.wanandroid.com/blogimgs/acc23063-1884-4925-bdf8-0b0364a7243e.png")
    }


    fun loadEntities() = loadImages().mapIndexed { index, s -> BannerEntity(index, s) }
}

