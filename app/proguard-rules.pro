# Add project specific ProGuard rules here.
-keep class com.moviestreamer.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
