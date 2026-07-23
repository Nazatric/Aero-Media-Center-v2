# Minification is currently disabled in app/build.gradle.kts (isMinifyEnabled = false)
# because R8 OOMs on GitHub's free hosted CI runners with this dependency graph.
# These rules are here ready to go for whenever it's re-enabled on a runner with
# more memory (see README -> Build & CI).

# Media3 / ExoPlayer
-keep class androidx.media3.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
