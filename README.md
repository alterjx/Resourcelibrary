项目概况

整体包括：后台系统，支付系统，爬虫系统和android apk项目；

后台系统：使用的技术java（servlet+hibernate），为app提供数据，业务比较简单，使用的也是基本的javaweb开发技术，不好意思开源啦；

支付系统：直接使用的pay-java-parent这个开源项目，有兴趣的可以看看https://github.com/egzosn/pay-java-parent；

爬虫系统：使用的开源的爬虫项目：webmagic，主要用于爬取网站图片的链接地址存入数据库；

android系统：最重要的就是这个android 闲猪手的项目啦，本人也是从事android开发的，这个项目还凑合着看，分享一下，菜鸟学习，大牛勿喷！

android闲猪手项目

apk下载：https://fir.im/pighand  注明*线上账号信息也可以用于自己打的release包，例如你使用线上APK升级了VIP，那么在自己打的apk上登录也是VIP


项目亮点

网络库：RxJava+Retrofit,整体封装的还算可以吧，很方便替换底层网络库技术，参考了网上的开源项目https://github.com/wzgiceman/RxjavaRetrofitDemo-master；

项目缺点

业务代码冗余，没有很好的封装，例如图片的搜索页面（PicListFragment）和图片列表页面(SearchPicFragment),两个页面有好多可以抽取的业务处理逻辑。当然便于菜鸟入门理解本项目，后期可以拿本项目作为业务重构的练习项目，哈哈。









参考开源项目

https://github.com/wzgiceman/RxjavaRetrofitDemo-master

https://github.com/egzosn/pay-java-parent

https://github.com/CarGuo/GSYVideoPlayer

