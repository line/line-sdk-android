-keep class com.linecorp.linesdk.api.LineApiClientBuilder { *; }
-keepattributes *Annotation*

#### generic
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep ,includedescriptorclasses class com.linecorp.linesdk.widget.LoginButton { *; }

-dontwarn java.lang.invoke.*

#### okhttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn rx.**

-dontwarn org.spongycastle.jce.provider.X509LDAPCertStoreSpi
-dontwarn org.spongycastle.x509.util.LDAPStoreHelper

# start for jjwt library
-keep class io.jsonwebtoken.** { *; }

-keep class com.linecorp.linesdk.api.LineEnvConfig {
    *;
}

-keep class com.linecorp.linesdk.ManifestParser {
    public protected *;
}
