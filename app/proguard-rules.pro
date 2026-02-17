# Add project specific ProGuard rules here.
# Keep only Gson-annotated fields for proper serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data classes used with Retrofit/Gson
-keep class com.moviestreamer.data.Movie { *; }
-keep class com.moviestreamer.data.MoviesResponse { *; }
