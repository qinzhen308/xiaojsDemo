

**小教室 Android 端说明**
===================

    Xiaojs-android ：一套代码同时支持手机和平板


编译环境
----

 - Android Studio 2.2.2
 - gradle
 - jdk 1.8

> compileSdkVersion 24
> buildToolsVersion "24.0.3"
> minSdkVersion 15
> targetSdkVersion 24
   

        


----------

版本控制
-------------

 - GIT [中文教程](https://git-scm.com/book/zh/)
 - GITLAB


----------

GIT工作流
-------------

 - [Gitflow Workflow](http://nvie.com/posts/a-successful-git-branching-model/) [中文版参考](http://www.jianshu.com/p/104fa8b15d1e)
 
 ![enter image description here](http://nvie.com/img/git-model@2x.png)


----------

Code Review
-------------
代码审查通过版本控制平台的Pull Request方式进行。提交代码前，首先要进行Pull Request，审查通过后，方可进行正式提交。

> *注：Pull Request需要平台工具支持*


----------


代码规范
-------------
开发人需要严格遵守以下代码规范：

 - Java：[Google Java Style](https://google.github.io/styleguide/javaguide.html) [中文版](http://www.hawstein.com/posts/google-java-style.html)
 - 资源（Resources）文件命名，要遵循前缀表明类型的习惯，形如：type_foo_bar.xml。
 > 例如：fragment_contact_details.xml、
         view_primary_button.xml、
         activity_main.xml




----------


开发架构模式
-------
使用MVC架构
![enter image description here](http://www.jcodecraeer.com/uploads/20160414/1460565635729862.png)

> 其他MVP&MVVM架构，可参考[android-architecture](https://github.com/googlesamples/android-architecture)

----------


使用框架
----


REST API：

 - [Retrofit](http://square.github.io/retrofit)
 - [jackson](https://github.com/codehaus/jackson)

Network：

 - [OKHttp](http://square.github.io/okhttp)

Image Loader:

 - [Glide](https://github.com/bumptech/glide)

其他：

 - [ButterKnife](http://jakewharton.github.io/butterknife) 
 - [Rxjava](https://github.com/ReactiveX/RxJava)
 - [jsonschema2pojo](https://github.com/joelittlejohn/jsonschema2pojo)


----------

质量保证
----

 - 单元测试
 - 自动化测试
 - 性能检测

> 内存占用、CPU消耗等：[ADT](https://developer.android.com/studio/intro/index.html)

> 检测内存泄漏：[leakcanary](https://github.com/square/leakcanary)

> debug tools:[stetho](http://facebook.github.io/stetho/)

> 静态代码分析:[infer](http://fbinfer.com/)

 - Crash捕获

> APP需要跟踪捕获发生Crash时的信息，并上传至BUG服务器，以供开发人员实时解决问题。
APP端收集Crash并上报，使用第三方SDK：[Bugly](https://bugly.qq.com/v2/index)

 - 统计用户行为

> APP需要对用户使用行为及数据进行上传统计。
> 使用第三方统计SDK： [友盟](https://www.umeng.com/)


----------

日志处理
-------------
在APP的debug、内测、公测以及release这些不同的版本，所需的日志是不同的。
所以日志需要做全局封装，可以对不同版本进行快速的日志模式切换，并可以快速开启和关闭。


> android端使用日志框架： [Logger](https://github.com/orhanobut/logger)
 


----------

参考（thx）
-------------
> http://littlerobots.nl/blog/

> https://code.facebook.com/projects/







