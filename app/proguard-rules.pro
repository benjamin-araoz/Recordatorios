# Evita que R8 rompa las clases internas de Kotlin Coroutines (causa IllegalAccessError en release)
-keep class kotlinx.coroutines.** { *; }
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Firebase: evita que R8 elimine u ofusque las clases que usa internamente
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# BORRAR ARCHIVO