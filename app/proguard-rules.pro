# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
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
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法

# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames # 是否使用大小写混合
-dontskipnonpubliclibraryclasses
-verbose # 混淆时是否记录日志

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify  # 混淆时是否做预校验
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

#-library dyuproject
-keeppackagenames java.util.**
-dontwarn java.util.**

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclasseswithmembers class * {    # 保持自定义控件类不被混淆
    public <init>(android.content.Context);
}
-keepclasseswithmembers class * {   # 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留Parcelable序列化的类不能被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 保留Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

# 对R文件下的所有类及其方法，都不能被混淆
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 对于带有回调函数onXXEvent的，不能混淆
-keepclassmembers class * {
    void *(**On*Event);
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-keep class android.support.** { *; }

-keep class com.nineoldandroids.animation.** {*;}

-keep public class java.util.**{ *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.resource.app.utils.** {*;}

-keepclasseswithmembers class com.resource.app.api.** { *; }
-keep public class com.resource.app.api.** {
  private <fields>;
  public <fields>;
  public <methods>;
  }


-keepclasseswithmembers class com.resource.app.model.** { *; }
-keep public class com.resource.app.model.** {
  private <fields>;
  public <fields>;
  public <methods>;
  }


-keepclasseswithmembers class com.resource.app.manager.** { *; }
-keep public class com.resource.app.manager.** {
  private <fields>;
  public <fields>;
  public <methods>;
  }
-keepclasseswithmembers class com.resource.app.customview.** { *; }

-keepattributes *JavascriptInterface*

-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(android.view.View);
}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}



# Facebook
-keep class com.facebook.** {*;}
-keep interface com.facebook.** {*;}
-keep enum com.facebook.** {*;}


# Fresco
-keep class com.facebook.fresco.** {*;}
-keep interface com.facebook.fresco.** {*;}
-keep enum com.facebook.fresco.** {*;}

# See http://sourceforge.net/p/proguard/bugs/466/
-dontwarn com.facebook.infer.annotation.ReturnsOwnership
-dontwarn com.facebook.infer.annotation.Functional

#updatefun
-dontwarn cn.hugeterry.updatefun.**
-keep public class cn.hugeterry.updatefun.**{*;}

-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-keep class com.mob.**{*;}
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn **.R$*

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class com.resource.app.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


#ali
 -dontwarn com.alipay.**
#保持他们的类不被混淆
-keep class com.alipay.** { *;}
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

# banner 的混淆代码
-keep class com.youth.banner.** {
    *;
 }
 -keep class tv.danmaku.ijk.** { *; }
 -dontwarn tv.danmaku.ijk.**
 -keep class com.shuyu.gsyvideoplayer.** { *; }
 -dontwarn com.shuyu.gsyvideoplayer.**
