# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/maxiaobao/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#-keep public class * extends android.support.v4.app.**


# retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions


# jackson
-keepattributes *Annotation*
-keepnames class com.fasterxml.jackson.** { *; }
 -dontwarn com.fasterxml.jackson.databind.**
 -keep class org.codehaus.** { *; }
 -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }
# -keep public class cn.xiaojs.xma.model.** {
#
#  public void set*(***);
#  public *** get*();
#}

-keepclassmembers class cn.xiaojs.xma.model.** {
    <fields>;
    <methods>;
}

#
-keep class cn.xiaojs.xma.ui.classroom.whiteboard.core.ViewGestureListener$* {*;}

-keep class cn.xiaojs.xma.ui.widget.GooeyMenu$* {
    <fields>;
    <methods>;
}

-keep class cn.xiaojs.xma.common.permissiongen.** { *; }
-keep class cn.xiaojs.xma.common.crop.** {*;}

# Use reflection to invoke get method,so keep method
-keep class okhttp3.Cache {
    <methods>;
}




# glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# qiniu
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

# live
-keep class com.qiniu.pili.droid.streaming.** { *; }
-keep class com.pili.pldroid.player.** { *; }
-keep class tv.danmaku.ijk.media.player.** {*;}


# bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}