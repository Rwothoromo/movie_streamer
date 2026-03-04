# Add project specific ProGuard rules here.

# ── Gson / Retrofit data models ──────────────────────────────────────────────
# Keep all fields annotated with @SerializedName so Gson can deserialize them.
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep all data/model classes in the data package to prevent Gson breakage.
-keep class com.moviestreamer.data.** { *; }

# ── Retrofit ─────────────────────────────────────────────────────────────────
-keepattributes Signature
-keepattributes Exceptions
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**

# ── OkHttp ───────────────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ── Koin ─────────────────────────────────────────────────────────────────────
-keep class org.koin.** { *; }
-keepnames class * extends org.koin.core.module.Module

# ── ExoPlayer / Media3 ───────────────────────────────────────────────────────
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# ── Coil ─────────────────────────────────────────────────────────────────────
-dontwarn coil.**

# ── Room ─────────────────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# ── Kotlin coroutines ────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ── Kotlin metadata (needed for reflection) ──────────────────────────────────
-keepattributes *Annotation*
-keepattributes InnerClasses
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
