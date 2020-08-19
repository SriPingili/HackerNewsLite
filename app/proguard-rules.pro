# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class okhttp3.* {*;}
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class retrofit2.* { *; }
-dontwarn retrofit2.**

-keep class sp.android.hackernewslite.play.model.HackerStory { *; }
-keep class sp.android.hackernewslite.play.email.SendMail { *; }
-keep class sp.android.hackernewslite.play.ui.fragments.FeedbackFragment {*;}

#https://stackoverflow.com/questions/5434699/android-javamail-and-proguard
-dontwarn java.awt.**
-dontwarn java.beans.Beans
-dontwarn javax.security.**

-keep class javamail.* {*;}
-keep class javax.mail.* {*;}
-keep class javax.activation.* {*;}

-keep class com.sun.mail.dsn.* {*;}
-keep class com.sun.mail.handlers.* {*;}
-keep class com.sun.mail.smtp.* {*;}
-keep class com.sun.mail.util.* {*;}
-keep class mailcap.* {*;}
-keep class mimetypes.* {*;}
-keep class myjava.awt.datatransfer.* {*;}
-keep class org.apache.harmony.awt.* {*;}
-keep class org.apache.harmony.misc.* {*;}

-keep class androidx.appcompat.widget.SearchView { *; }

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.* {
    volatile <fields>;
}