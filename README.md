<div align="center">

# MediaPicker

### Video/Picture Fetch from Android device, alternative to the default file manager

English
&nbsp;&nbsp;| &nbsp;&nbsp;
<a href="https://github.com/luisangeldd/MediaPicker/blob/main/README-es.md">Spanish</a>

<div align="left">

## 📱 Screenshots

<div align="center">
<div>
<img src="metadata/img1.jpg"  width="30%" />
<img src="metadata/img2.jpg"  width="30%" />
<img src="metadata/img3.jpg"  width="30%" />
<img src="metadata/img4.jpg"  width="30%" />
</div>
</div>
 
<br>

## 📱 Video

<div align="center">
<div>
<img width="" src="metadata/video.gif"  width="100%" />
</div>
</div>

<br>

## 📖 Features

- Fetch videos and pictures/images files from android device.

- One click to select any media, from your device.

- Easy to use and user-friendly.

- Inject dependency whit Koin, simple and fast.

- [Material Design 3](https://m3.material.io/) style UI.

## ⬇️ How to

Step 1. Add the dependency

- Groovy

```groovy
dependencies {
    implementation 'com.github.luisangeldd:MediaPicker:Tag'
}
```
- Kotlin DSL

```kotlin
dependencies {
    implementation("com.github.luisangeldd:MediaPicker:Tag")
}
```
Step 2. Use

In you gradle package add the next dependencies
```kotlin
dependencies {
   ...
    implementation("io.insert-koin:koin-android:Tag")
    implementation("io.insert-koin:koin-androidx-navigation:Tag")
    implementation("io.insert-koin:koin-androidx-compose:Tag")
}
```
Create a applicacion class to inject the modules
```kotlin
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}
```
In your manifest add the next 
```kotlin
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    <!-- Required only if your app needs to access images or photos
    that other apps created. -->
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

    <!-- Required only if your app needs to access videos
         that other apps created. -->
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <application
        android:name=".App"
        android:requestLegacyExternalStorage="true"
        android:requestRawExternalStorageAccess="true"
        ...
        <provider
                android:name="androidx.core.content.FileProvider"
                android:authorities="${applicationId}.provider"
                android:exported="false"
                android:grantUriPermissions="true">
                <meta-data
                    android:name="android.support.FILE_PROVIDER_PATHS"
                    android:resource="@xml/provider_paths" />
        </provider>
    </application>
</manifest>
```
