<div align="center">

# MediaPicker

### Obtenga archivos de video/imagen de su dispositivo Android, alternativa al administrador de archivos predeterminado de Android.

Español
&nbsp;&nbsp;| &nbsp;&nbsp;
<a href="https://github.com/luisangeldd/MediaPicker/blob/main/README-en.md">English</a>

<div align="left">

## 📱 Capturas

<div align="center">
<div>
<img src="metadata/img1.jpg"  width="30%" />
<img src="metadata/img2.jpg"  width="30%" />
<img src="metadata/img3.jpg"  width="30%" />
<img src="metadata/img4.jpg"  width="30%" />
</div>
</div>
 
<br>

## 📱 Videos

<div align="center">
<div>
<img width="" src="metadata/video.gif"  width="100%" />
</div>
</div>

<br>

## 📖 Características

- Obtenga los archivos de video/imágenes de su dispositivo Android.

- Un clic para seleccionar cualquier medio de su dispositivo.

- Fácil de usar y amigable.

- Inyeccion de dependencia con Koin o Dagger Hilt, simple y rápido.

- Interfaz de usuario de estilo [Material Design 3](https://m3.material.io/).

- Sólo Compose y Kotlin.

- Inspirado en el [selector de fotos](https://developer.android.com/training/data-storage/shared/photopicker?hl=es-419) de Android.

## ⬇️ Cómo implemetar

Paso 1. Agregar las dependencias

- Groovy

```groovy
dependencies {
    ...
    implementation 'com.github.luisangeldd:MediaPicker:Tag'
    implementation 'io.insert-koin:koin-android:Tag'
    implementation 'io.insert-koin:koin-androidx-navigation:Tag'
    implementation 'io.insert-koin:koin-androidx-compose:Tag'
}
```
- Kotlin DSL

```kotlin
dependencies {
    ...
    implementation("com.github.luisangeldd:MediaPicker:Tag")
    implementation("io.insert-koin:koin-android:Tag")
    implementation("io.insert-koin:koin-androidx-navigation:Tag")
    implementation("io.insert-koin:koin-androidx-compose:Tag")
}
```
Paso 2. Crear una clase de aplicación para inyectar los módulos.
- Si usaras Koin en tu proyecto para implementar la inyección de dependencias usa la siguiente estructura
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
- Si usaras Dagger Hilt en tu proyecto para implementar la inyección de dependencias usa la siguiente estructura
```kotlin
@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        
    }
}
```
Paso 3. Configura tu archivo de manifiesto
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
Paso 4. Crear el archivo proveedor_paths.xml
<br>
El archivo proveedor_paths.xml se ubicara en res/xml de su aplicación y contendra lo siguiente
```kotlin
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <root-path name="root" path="." />
    <external-path
        name="external"
        path="." />
    <external-files-path
        name="external_files"
        path="/" />
    <cache-path
        name="cache"
        path="." />
    <external-cache-path
        name="external_cache"
        path="." />
    <files-path
        name="files"
        path="." />
</paths>
```
Paso 5. Usar en tu aplicación
- Si usaras Koin en tu proyecto para implementar la inyección de dependencias usa la siguiente estructura
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNameTheme {
                MediaPicker(
                    getMedia = {}
                )
            }
        }
    }
}
```
- Si usaras Dagger Hilt en tu proyecto para implementar la inyección de dependencias usa la siguiente estructura
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNameTheme {
                MediaPicker(
                    getMedia = {}
                )
            }
        }
    }
}
```
