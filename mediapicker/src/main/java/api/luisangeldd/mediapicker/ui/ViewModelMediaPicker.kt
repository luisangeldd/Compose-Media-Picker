package api.luisangeldd.mediapicker.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.luisangeldd.mediapicker.core.StatePicker
import api.luisangeldd.mediapicker.core.StateRequest
import api.luisangeldd.mediapicker.core.StatusRequest
import api.luisangeldd.mediapicker.data.model.Media
import api.luisangeldd.mediapicker.data.model.MediaUser
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import api.luisangeldd.mediapicker.data.MediaPickerUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModelMediaPicker (private val mediaPickerUseCase: MediaPickerUseCase): ViewModel(){
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