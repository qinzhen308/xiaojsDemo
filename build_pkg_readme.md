**Xiaojs android client build Description**
===================

    Xiaojs-android ：一套代码同时支持手机和平板


infer 静态代码分析

Android Studio 可以帮助查找未使用、却使 APK 变得臃肿的资源。 点击 Analyze > Run Inspection by Name > Unused Resources，Android Studio 将识别任何已知代码路径未使用的资源。 必须首先删除所有未使用的代码，因为死代码“占用”的资源无法被识别为未使用。

对所有 PNG 资源运行 zopflipng

Android Studio APK 分析器

Release
---------
 - 检查XiaojsConfig里面的配置，确保切换到release模式。
 - 将演示模式关闭
 - 首先将Debug日志关掉
 - 修改版本号
 - 配置渠道号


 Build final
 ---------
 > $ ./gradlew clean assembleReleaseChannels -PchannelFile=channel

    注意：生成release包后，要对混淆映射文件Mapping.txt等进行保存备份。
    git tag -a v1.4 -m 'my version 1.4'
    git tag -v v1.4.2.1
    git push origin --tags







