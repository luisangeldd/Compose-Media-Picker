package api.luisangeldd.mediapicker.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.luisangeldd.mediapicker.core.AnswerOfRequest
import api.luisangeldd.mediapicker.core.MediaPickerModule
import api.luisangeldd.mediapicker.core.MediaPickerModuleImpl
import api.luisangeldd.mediapicker.core.StateOfRequest
import api.luisangeldd.mediapicker.core.StatePicker
import api.luisangeldd.mediapicker.data.model.Media
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class ViewModelMediaPicker (
    context: Context
): ViewModel(){
    private val mediaPickerModule: MediaPickerModule = MediaPickerModuleImpl(context)
    private val _statePicker = MutableStateFlow(StatePicker.CLOSE)
    val statePicker: StateFlow<StatePicker> = _statePicker
    private var _dataOfMedia = MutableStateFlow(Media())
    val dataOfMedia : StateFlow<Media> = _dataOfMedia
    private val _mediaSelected = MutableStateFlow<List<MediaUserV0>>(mutableListOf())
    val mediaSelected: StateFlow<List<MediaUserV0>> = _mediaSelected
    private val _mediaJob = MutableStateFlow<Job?>(null)
    private val _albumJob = MutableStateFlow<Job?>(null)
    private val _mediaByAlbumJob = MutableStateFlow<Job?>(null)
    private val requestMedia: () -> Job = {
        viewModelScope.launch {
            _dataOfMedia.value.run {
                _dataOfMedia.value = (
                    copy(
                        media = media.copy(
                            stateOfRequestMedia =  StateOfRequest.START
                        )
                    )
                )
            }
            val data = mediaPickerModule.mediaPickerUseCase.fetchMedia()
            _dataOfMedia.value.run {
                _dataOfMedia.value = (
                    copy(
                        media = media.copy(
                            media = data.ifEmpty { emptyList() },
                            answerOfRequestMedia = if (data.isEmpty()) AnswerOfRequest.EMPTY else AnswerOfRequest.NOT_EMPTY,
                            stateOfRequestMedia =  StateOfRequest.END
                        )
                    )
                )
            }
        }
    }
    private val requestAlbum: () -> Job = {
        viewModelScope.launch {
            _dataOfMedia.value.run {
                _dataOfMedia.value = (
                        copy(
                            album = album.copy(
                                stateOfRequestAlbum = StateOfRequest.START
                            )
                        )
                        )
            }
            val data =
                mediaPickerModule.mediaPickerUseCase.fetchAlbums(dataOfMedia.value.media.media)
            _dataOfMedia.value.run {
                _dataOfMedia.value = (
                        copy(
                            album = album.copy(
                                album = data.ifEmpty { emptyList() },
                                answerOfRequestAlbum = if (data.isEmpty()) AnswerOfRequest.EMPTY else AnswerOfRequest.NOT_EMPTY,
                                stateOfRequestAlbum = StateOfRequest.END
                            )
                        )
                        )
            }
        }
    }
    private val requestMediaByAlbum: (String) -> Job = {
        viewModelScope.launch {
            _dataOfMedia.value.run {
                _dataOfMedia.value = (
                    copy(
                        mediaByAlbum = mediaByAlbum.copy(
                            stateOfRequestMediaByAlbum = StateOfRequest.START
                        )
                    )
                )
            }
            val data = mediaPickerModule.mediaPickerUseCase.fetchMediaByAlbum(it)
            _dataOfMedia.value.run {
                _dataOfMedia.value = (
                    copy(
                        mediaByAlbum = mediaByAlbum.copy(
                            mediaByAlbum = data.ifEmpty { emptyList() },
                            answerOfRequestMediaByAlbum = if (data.isEmpty()) AnswerOfRequest.EMPTY else AnswerOfRequest.NOT_EMPTY,
                            stateOfRequestMediaByAlbum = StateOfRequest.END
                        )
                    )
                )
            }
        }
    }
    fun getMedia() {
        viewModelScope.launch {
            if (_mediaJob.value == null){
                _mediaJob.value = requestMedia()
            } else {
                if (_mediaJob.value!!.isCompleted){
                    _mediaJob.value = null
                    getMedia()
                }
            }
        }
    }
    fun getAlbums() {
        viewModelScope.launch {
            if (_albumJob.value == null){
                _albumJob.value = requestAlbum()
            } else {
                if (_albumJob.value!!.isCompleted){
                    _albumJob.value = null
                    getMedia()
                }
            }
        }
    }
    fun getMediaByAlbum(folder:String) {
        viewModelScope.launch {
            if (_mediaByAlbumJob.value == null){
                _mediaByAlbumJob.value = requestMediaByAlbum(folder)
            } else {
                if (_mediaByAlbumJob.value!!.isCompleted){
                    _mediaByAlbumJob.value = null
                    getMediaByAlbum(folder)
                }
            }
        }
    }
    suspend fun getThumbnail(
        uri : Uri,
        id : Long,
        mimeType : String,
        resolutionHeight: Boolean = true
    ) = viewModelScope.async {
        mediaPickerModule.mediaPickerUseCase.fetchThumbnail(
            uri,
            id,
            mimeType
            ,resolutionHeight
        )
    }.await()

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
    fun clearMedia(){
        viewModelScope.launch {
            cancelJobs()
        }
    }
    private fun cancelJobs(){
        if (_mediaJob.value != null) {
            _mediaJob.value!!.cancel()
            _mediaJob.value = null
        }
        if (_albumJob.value != null) {
            _albumJob.value!!.cancel()
            _albumJob.value = null
        }
        if (_mediaByAlbumJob.value != null) {
            _mediaByAlbumJob.value!!.cancel()
            _mediaByAlbumJob.value = null
        }
    }
    override fun onCleared() {
        super.onCleared()
        cancelJobs()
    }
}
