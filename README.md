<img src="sdklogo.png" width="355" height="97">

# LINE SDK for Android #

## Overview ##
The LINE SDK for Android provides a modern way of implementing LINE APIs. The features included in this SDK will help you develop an Android app with engaging and personalized user experience.

## Features ##
The LINE SDK for Android provides the following features.

### User authentication ###
This feature allows users to log in to your service with their LINE accounts. With the help of the LINE SDK for Android, it has never been easier to integrate LINE Login into your app. Your users will automatically log in to your app without entering their LINE credentials if they are already logged in to LINE on their Android devices. This offers a great way for users to get started with your app without having to go through a registration process.

### Utilizing user data with OpenID support ###
Once the user is authorized, you can get the user’s LINE profile. You can utilize the user's information registered in LINE without building your user system.

The LINE SDK supports the OpenID Connect 1.0 specification. You can get ID tokens that contain the user’s LINE profile when you retrieve the access token.

## Using the SDK ##
To use the LINE SDK with your Android app, follow the steps below.

* Create a channel. 
* Integrate LINE Login into your Android app using the SDK. 
* Make API calls from your app using the SDK or from server-side through the Social API. 

For more information, refer to the [LINE SDK for Android guide](https://developers.line.biz/en/docs/android-sdk/) on the [LINE Developers site](https://developers.line.biz).

### Trying the starter app ###
To have a quick look at the features of the LINE SDK, try our starter app by following the steps below:

1. Clone the repository.

    ```git clone https://github.com/line/line-sdk-android.git```

1. Build the starter app.

    `./gradlew app:assembleDebug`

The starter app apk file will be built as `app/build/outputs/apk/debug/app-debug.apk`.

## Contributing

If you believe you have discovered a vulnerability or have an issue related to security, please **DO NOT** open a public issue. Instead, send us a mail to [dl_oss_dev@linecorp.com](mailto:dl_oss_dev@linecorp.com).

For contributing to this project, please see [CONTRIBUTING.md](https://github.com/line/line-sdk-android/blob/master/CONTRIBUTING.md).
