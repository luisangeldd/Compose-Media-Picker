package com.luisangeldd.mediapicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.luisangeldd.mediapicker.MediaUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel:ViewModel() {
    private val _mediaSelectedUser = MutableStateFlow<List<MediaUser>>(mutableListOf())
    val mediaSelectedUser: StateFlow<List<MediaUser>> = _mediaSelectedUser

    fun getMedia(mediaUser: List<MediaUser>){
        viewModelScope.launch {
            _mediaSelectedUser.value = mediaUser
        }
    }
}