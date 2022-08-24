<img src="sdklogo.png" width="355" height="97">

[![Maven Central](https://img.shields.io/maven-central/v/com.linecorp.linesdk/linesdk.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.linecorp.linesdk%22%20AND%20a:%22linesdk%22)

# Overview #
The LINE SDK for Android provides a modern way of implementing LINE APIs. The features included in this SDK will help you develop an Android app with engaging and personalized user experience.

# Features #
The LINE SDK for Android provides the following features.

### User authentication ##
This feature allows users to log in to your service with their LINE accounts. With the help of the LINE SDK for Android, it has never been easier to integrate LINE Login into your app. Your users will automatically log in to your app without entering their LINE credentials if they are already logged in to LINE on their Android devices. This offers a great way for users to get started with your app without having to go through a registration process.

### Utilizing user data with OpenID support ###
Once the user is authorized, you can get the user’s LINE profile. You can utilize the user's information registered in LINE without building your user system.

The LINE SDK supports the OpenID Connect 1.0 specification. You can get ID tokens that contain the user’s LINE profile when you retrieve the access token.

# Setup #
### Pre-request
Create your own LINE Channel and follow the instructions [here](https://developers.line.biz/en/docs/android-sdk/integrate-line-login/) to link your app to your channel.

### Gradle

Add mavenCentral to your repositories if it's not added yet.

```gradle
repositories {
    ...
	mavenCentral()
}
```

Import line-sdk dependency

```
dependencies {
    implementation 'com.linecorp.linesdk:linesdk:$linesdk_version'
    ...
}

```


# Quickstart

A pre-defined LINE login button is provided. You can add it to the user interface of your app to provide your users with a quick way to log in as below

### Add login buton to layout xml


```xml
<com.linecorp.linesdk.widget.LoginButton
    android:id="@+id/line_login_btn"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

### Use it in codes
```java
LoginButton loginButton = rootView.findViewById(R.id.line_login_btn);

// if the button is inside a Fragment, this function should be called.
loginButton.setFragment(this); 

// replace the string to your own channel id.
loginButton.setChannelId("the channel id you created");

// configure whether login process should be done by LINE App, or by WebView.
loginButton.enableLineAppAuthentication(true);

// set up required scopes. 
loginButton.setAuthenticationParams(new LineAuthenticationParams.Builder()
        .scopes(Arrays.asList(Scope.PROFILE))
        .build()
);

// A delegate for delegating the login result to the internal login handler. 
private LoginDelegate loginDelegate = LoginDelegate.Factory.create();
loginButton.setLoginDelegate(loginDelegate);

loginButton.addLoginListener(new LoginListener() {
    @Override
    public void onLoginSuccess(@NonNull LineLoginResult result) {
        Toast.makeText(getContext(), "Login success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFailure(@Nullable LineLoginResult result) {
        Toast.makeText(getContext(), "Login failure", Toast.LENGTH_SHORT).show();
    }
});

```


For more information, refer to the [LINE SDK for Android guide](https://developers.line.biz/en/docs/android-sdk/) on the [LINE Developers site](https://developers.line.biz).


# Try the starter app
To have a quick look at the features of the LINE SDK, try our starter app by following the steps below:

1. Clone the repository.

    ```git clone https://github.com/line/line-sdk-android.git```

1. Build the starter app.

    `./gradlew app:assembleDebug`

The starter app apk file will be built as `app/build/outputs/apk/debug/app-debug.apk`.

# Contributing

If you believe you have discovered a vulnerability or have an issue related to security, please **DO NOT** open a public issue. Instead, send us a mail to [dl_oss_dev@linecorp.com](mailto:dl_oss_dev@linecorp.com).

For contributing to this project, please see [CONTRIBUTING.md](https://github.com/line/line-sdk-android/blob/master/CONTRIBUTING.md).
