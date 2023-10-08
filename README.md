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

https://github.com/luisangeldd/MediaPicker/assets/94653501/6e0f8c60-3114-4800-9f65-567bf58fecc8

</div>

<br>

## 📖 Características

- Obtenga los archivos de video/imágenes de su dispositivo Android.

- Un clic para seleccionar cualquier medio de su dispositivo.

- Fácil de usar y amigable.

- Inyeccion de dependencias con Koin o Dagger Hilt, simple y rápido.

- Interfaz de usuario de estilo [Material Design 3](https://m3.material.io/).

- Sólo Compose y Kotlin.

- Inspirado en el [selector de fotos](https://developer.android.com/training/data-storage/shared/photopicker?hl=es-419) de Android.

## 📖 Características Futuras

- Mostrar carpetas del dispostivo y su contenido "Para algunos usuarios puede ser importante el poder acceder a carpetas especificas y buscar un contenido especifico".

- Limitar la cantidad de items que es posible seleccionar "En algunos proyectos puede ser neesario el limitar la cantidad de archivos que es posible seleccionar ya sea por que seran enviados a algun repositorio y el espacio es importante".
  

## ⬇️ Cómo implemetar

Paso 1. Agregar las dependencias
<br>
Si usaras Koin en tu proyecto para implementar la inyección de dependencias usa la siguiente estructura, a la version se le asigna una K más la version (K1.0.0)

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

Si usaras  Dagger Hilt en tu proyecto para implementar la inyección de dependencias usa la siguiente estructura, a la version se le asigna una H más la version (H1.0.0)
<br>
- Groovy
```groovy
plugins {
    id 'com.google.dagger.hilt.android'
    id 'kotlin-kapt'
}
```
```groovy
dependencies {
    ...
    implementation 'com.github.luisangeldd:MediaPicker:Tag'
    implementation 'androidx.hilt:hilt-navigation-compose:Tag'
    implementation 'com.google.dagger:hilt-android:Tag'
    kapt 'com.google.dagger:hilt-compiler:Tag'
}
```
- Kotlin DSL
```kotlin
plugins {
    ...
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}
```
```kotlin
dependencies {
    ...
    implementation("com.github.luisangeldd:MediaPicker:Tag")
    implementation("androidx.hilt:hilt-navigation-compose:Tag")
    implementation("com.google.dagger:hilt-android:Tag")
    kapt("com.google.dagger:hilt-compiler:Tag")
}
```
Paso 2. Crear una clase de aplicación para inyectar los módulos.
- Si usaras Koin usa la siguiente estructura
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
- Si usaras Dagger Hilt usa la siguiente estructura
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
        ...
    </application>
</manifest>
```

Paso 4. Usar en tu aplicación
<br>
- Si usaras Koin usa la siguiente estructura
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
- Si usaras Dagger Hilt usa la siguiente estructura
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
## 🧱 Creditos
- [kotlin](https://kotlinlang.org/)
- [MediaStore](https://developer.android.com/reference/android/provider/MediaStore)
- [Google Fonts:Icons](https://fonts.google.com/icons)

## Licencia

    Copyright 2023 luisangeldd
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
