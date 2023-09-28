package com.luisangeldd.mediapicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import api.luisangeldd.mediapicker.MediaPicker
import com.luisangeldd.mediapicker.ui.theme.MediaPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel = viewModel<MainViewModel>()
            val mediaUser by mainViewModel.mediaSelectedUser.collectAsState()
            MediaPickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        MediaPicker(
                            getMedia = mainViewModel::getMedia
                        )
                        LazyColumn(content = {
                            items(mediaUser){
                                Text(text = "${it.fileMedia}")
                            }
                        })
                    }
                }
            }
        }
    }
}
