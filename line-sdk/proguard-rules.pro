-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*, Signature, InnerClasses, SourceFile, LineNumberTable, EnclosingMethod
-dontobfuscate

-keep class com.linecorp.linesdk.* { *; }
-keep class com.linecorp.linesdk.api.* { *; }
-keep class com.linecorp.linesdk.auth.* { *; }
-keep class com.linecorp.linesdk.widget.** { *; }
-keep class com.linecorp.linesdk.message.** { *; }
-keep class com.linecorp.linesdk.utils.* { *; }

-dontwarn java.lang.invoke.*

# start for jjwt library
-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.* { *; }
-keepnames interface io.jsonwebtoken.* { *; }

-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**
# end for jjwt library

