package api.luisangeldd.mediapicker.h

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.luisangeldd.mediapicker.h.data.MediaPickerUseCase
import api.luisangeldd.mediapicker.h.data.model.Media
import api.luisangeldd.mediapicker.h.data.model.MediaUserV0
import api.luisangeldd.mediapicker.h.utils.StatePicker
import api.luisangeldd.mediapicker.h.utils.StateRequest
import api.luisangeldd.mediapicker.h.utils.StatusRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaViewModel @Inject constructor (private val mediaPickerUseCase: MediaPickerUseCase): ViewModel() {

    val isGranted = mutableStateOf(false)
    private val _statePicker = MutableStateFlow(StatePicker.CLOSE)
    val statePicker: StateFlow<StatePicker> = _statePicker

    private val _stateRequestMedia = MutableStateFlow<StateRequest>(StateRequest.IDLE)
    val stateRequestMedia: StateFlow<StateRequest> = _stateRequestMedia
    private val _statusRequestMedia = MutableStateFlow<StatusRequest>(StatusRequest.IDLE)
    val statusRequestMedia: StateFlow<StatusRequest> = _statusRequestMedia

    private val _media = MutableStateFlow<List<Media>>(mutableListOf())
    val media: StateFlow<List<Media>> = _media

    private val _mediaSelected = MutableStateFlow<List<MediaUserV0>>(mutableListOf())
    val mediaSelected: StateFlow<List<MediaUserV0>> = _mediaSelected

    private val _mediaSelectedUser = MutableStateFlow<List<MediaUser>>(mutableListOf())
    val mediaSelectedUser: StateFlow<List<MediaUser>> = _mediaSelectedUser

    fun onPermissionResult(
        isGranted: Boolean
    ) {
        this.isGranted.value = isGranted
    }
    fun getMedia() {
        viewModelScope.launch {
            _stateRequestMedia.value = StateRequest.START
            val data = mediaPickerUseCase.fetchMedia()
            _media.value = when {
                data.isEmpty() -> {
                    _statusRequestMedia.value = StatusRequest.EMPTY
                    emptyList()
                }
                else -> {
                    _statusRequestMedia.value = StatusRequest.NOT_EMPTY
                    data
                }
            }
            delay(1000)
            _stateRequestMedia.value = StateRequest.END
        }
    }
    fun getThumbnail(uri : Uri, id : Long, mimeType : String) = mediaPickerUseCase.fetchThumbnail(uri, id, mimeType)

    fun setMedia(data: List<MediaUserV0>) {
        viewModelScope.launch {
            _mediaSelected.value = data
        }
    }
    fun statePicker(state: StatePicker) {
        viewModelScope.launch {
           _statePicker.value = state
        }
    }
}

