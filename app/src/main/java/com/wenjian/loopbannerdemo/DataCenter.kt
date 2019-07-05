package com.wenjian.loopbannerdemo

/**
 * Description: DataCenter
 * Date: 2018/12/7
 *
 * @author jian.wen@ubtrobot.com
 */

object DataCenter {
    fun loadImages() = loadEntities().map { it.url }


    fun loadEntities() = arrayListOf<BannerEntity>().apply {
        add(BannerEntity(0,"http://www.wanandroid.com/blogimgs/ab17e8f9-6b79-450b-8079-0f2287eb6f0f.png","看看别人的面经，搞定面试"))
        add(BannerEntity(1,"http://www.wanandroid.com/blogimgs/fb0ea461-e00a-482b-814f-4faca5761427.png","兄弟，要不要挑个项目学习下"))
        add(BannerEntity(2,"http://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png","我们新增了一个常用导航Tab~"))
        add(BannerEntity(3,"http://www.wanandroid.com/blogimgs/00f83f1d-3c50-439f-b705-54a49fc3d90d.jpg","公众号文章列表强势上线"))
        add(BannerEntity(4,"http://www.wanandroid.com/blogimgs/90cf8c40-9489-4f9d-8936-02c9ebae31f0.png","JSON工具"))
//        add(BannerEntity(0,"http://www.wanandroid.com/blogimgs/acc23063-1884-4925-bdf8-0b0364a7243e.png","微信文章合集"))
    }
}

