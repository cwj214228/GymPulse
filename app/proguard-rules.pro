# 铁壳训练 · ProGuard Rules

# Room
-keep class com.gympulse.app.data.entity.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Kotlin
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-dontnote kotlinx.coroutines.**
