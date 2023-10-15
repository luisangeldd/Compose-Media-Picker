package com.luisangeldd.mediapicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import api.luisangeldd.mediapicker.ui.MediaPicker
import com.luisangeldd.mediapicker.ui.theme.MediaPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //val mainViewModel = viewModel<MainViewModel>()
            //val mediaUser by mainViewModel.mediaSelectedUser.collectAsState()
            MediaPickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        MediaPicker(
                            mediaPickerUseCase = App.mediaPickerModule.mediaPickerUseCase,
                            getMedia = {

                            }
                        )
                        /*LazyColumn(content = {
                            items(mediaUser){
                                Text(text = "${it.fileMedia}")
                            }
                        })*/
                    }
                }
            }
        }
    }
}
