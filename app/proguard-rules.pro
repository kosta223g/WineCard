# Retrofit's consumer rules and kotlinx.serialization's consumer rules are supplied by dependencies.
# Keep source information for readable retraced release crashes.
-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,*Annotation*
# ViewModels are constructed by AndroidX's default factory.
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
