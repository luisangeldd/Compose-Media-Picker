package api.luisangeldd.mediapicker

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.luisangeldd.mediapicker.data.MediaPickerUseCase
import api.luisangeldd.mediapicker.data.model.Media
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import api.luisangeldd.mediapicker.utils.StateRequest
import api.luisangeldd.mediapicker.utils.StatusRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MediaViewModel (private val mediaPickerUseCase: MediaPickerUseCase): ViewModel() {

    val isGranted = mutableStateOf(false)

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
                    Log.d("data","empty")
                    emptyList()
                }
                else -> {
                    _statusRequestMedia.value = StatusRequest.NOT_EMPTY
                    Log.d("data","not_empty")
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
            //_mediaSelected.value = data
            //if (data.isNotEmpty())
                //_mediaSelectedUser.value = data.map { mp -> MediaUser(mp.uriMedia,mp.fileMedia) }
            //else
                //_mediaSelectedUser.value = emptyList()
        }
    }
    fun removeMedia(){
        viewModelScope.launch {

        }
    }
}

