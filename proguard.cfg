-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.google.android.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

#Compatibility library
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.app.Fragment
-dontwarn **CompatHoneycomb
-keep class android.support.v4.** { *; }
#End Compatibility library

#ActionBarSherlock
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }
#End ActionBarSherlock

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#Saved
-keepclassmembers class * {
   public void *(android.view.View);
}

#EXTRACTED FROM ORIGINAL PROGUARD GENERAL CONFIG IN /tools/proguard
###################################################################
#-keepattributes *Annotation*
-keepattributes *Annotation*,Signature

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

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

#Problem with admob 6.2.1.jar
#-dontwarn com.google.ads.**

#edtFTP junit dependencies in test classes
-dontwarn junit.**

-dontwarn org.slf4j.**
-dontwarn org.json.*
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.codec.binary.**
-dontwarn com.enterprisedt.net.ftp.test.**

#Classes in libray jars
-keep class ca.benow.** { *; }
-keep class com.enterprisedt.** { *; }
-keep class com.vikingbrain.nmt.** { *; }
-keep class org.achartengine.** { *; }
-keep class org.json.** { *; }
-keep class org.simpleframework.** { *; }
#-keep class org.slf4j.** { *; }
