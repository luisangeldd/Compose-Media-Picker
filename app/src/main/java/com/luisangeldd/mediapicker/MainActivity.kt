package com.luisangeldd.mediapicker

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import api.luisangeldd.mediapicker.data.model.MediaUser
import api.luisangeldd.mediapicker.ui.MediaPicker
import com.luisangeldd.mediapicker.ui.theme.MediaPickerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            // control de boton hacia atras pruebas -- inicio
            /*var backPressCount by rememberSaveable { mutableIntStateOf(0) }
            var resetCounterJob by remember { mutableStateOf<Job?>(null) }
            val resetCounterAfterDelay: (Int) -> Job = {
                val resetDelayMillis = 5000L
                CoroutineScope(Dispatchers.IO).launch {
                    delay(resetDelayMillis)
                    backPressCount = 0
                }
            }
            val backPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val backCallback = remember {
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        backPressCount++
                        if (backPressCount >= 3) {
                            Log.d("backStack","backStack: finish")
                            finishAndRemoveTask()
                        }
                        resetCounterJob?.cancel()
                        resetCounterJob = resetCounterAfterDelay(backPressCount)
                    }
                }
            }
            DisposableEffect(key1 = backPressedDispatcher) {
                Log.d("backStack","backStack: OnBackPressedCallback")
                backPressedDispatcher?.addCallback(backCallback)

                onDispose {
                    Log.d("backStack","backStack: OnBackPressedCallback")
                    backCallback.remove()
                }
            }*/
            // control de boton hacia atras pruebas -- fin
            // permisos solicitados al telefono
            val mainViewModel by viewModels<MainViewModel>()
            val files by mainViewModel.mediaSelectedUser.collectAsState()
            val permissionsToRequest =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
                } else {
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            var action: () -> Unit ={} // funcion de disparo agregar en tu metodo para solicitar permisos cuando estos sean correctos
            var removeItem: (Int) -> Unit = {}
            var removeAllItems: () -> Unit = {}
            val scope = rememberCoroutineScope()
            // recuerda debes de solicilitar permisos de almacenamiento para poder acceder al contenidp  de tu telefono
            // en este caso se uso un rememberLauncherForActivityResult pero podrias usar cualquier otra libreria que conciderara adecuada
            // recuerda crear una funcion de disparo como "action" ya que esta recuperar la accion de mostrar el contenido
            val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { perms ->
                    scope.launch {
                        if (perms[permissionsToRequest[0]] == true && perms[permissionsToRequest[1]] == true) {
                            action() // funcion de disparo cuando se den los permisos al presionar el boton se consume el contenido de tu telefono
                        }
                    }
                }
            )
            MediaPickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        //Text(text = "veces precionado hacia atras: $backPressCount")
                        MediaPicker(
                            actionStart = {
                                action = it // action function recovering the action of opening the content
                            },
                            multiMedia = true,
                            showCarousel = true,
                            getMedia = { mainViewModel.getMedia(it) },
                            removeItem = {
                                removeItem = it
                            },
                            removeAllItems = {
                                removeAllItems = it
                            }
                        )
                        Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                            FilledTonalIconButton(
                                modifier = Modifier.size(100.dp),
                                onClick = { multiplePermissionResultLauncher.launch(permissionsToRequest) }
                            ) {
                                Icon(modifier = Modifier.size(100.dp),imageVector = Icons.Rounded.Add, contentDescription = null)
                            }
                        }
                        Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                            Button(onClick = { removeItem(1) }) {
                                Text(text = "Remove first item")
                            }
                        }
                        Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                            Button(onClick = { removeAllItems() }) {
                                Text(text = "Remove all items")
                            }
                        }
                        LazyColumn(content = {
                            items(files){
                                Text(text = "file: ${it.fileMedia}, uri: ${it.uriMedia}")
                            }
                        })
                        Log.d("getMedia","${files.size}")
                    }
                }
            }
        }
    }
}