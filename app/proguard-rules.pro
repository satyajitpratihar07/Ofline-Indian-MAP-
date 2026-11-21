# OfflineNav Proguard Rules

# Keep MapLibre Native SDK
-keep class org.maplibre.android.** { *; }
-dontwarn org.maplibre.android.**

# Keep Room entities and DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep Domain Models and Enums
-keep class com.offlinenav.domain.model.** { *; }
-keepclassmembers enum * { *; }

# Keep Gson serialization models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Timber
-dontwarn timber.log.**
