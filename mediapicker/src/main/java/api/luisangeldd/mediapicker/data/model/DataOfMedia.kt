package api.luisangeldd.mediapicker.data.model

import api.luisangeldd.mediapicker.core.AnswerOfRequest
import api.luisangeldd.mediapicker.core.StateOfRequest

internal data class DataOfMedia(
    val media: List<MediaData> = emptyList(),
    val stateOfRequestMedia: StateOfRequest = StateOfRequest.IDLE,
    val answerOfRequestMedia: AnswerOfRequest = AnswerOfRequest.IDLE,
)
internal data class DataOfAlbum(
    val album: List<AlbumData> = emptyList(),
    val stateOfRequestAlbum: StateOfRequest = StateOfRequest.IDLE,
    val answerOfRequestAlbum: AnswerOfRequest = AnswerOfRequest.IDLE,
)
internal data class DataOfMediaByAlbum(
    val mediaByAlbum: List<MediaData> = emptyList(),
    val stateOfRequestMediaByAlbum: StateOfRequest = StateOfRequest.IDLE,
    val answerOfRequestMediaByAlbum: AnswerOfRequest = AnswerOfRequest.IDLE,
)
internal data class Media(
    val media: DataOfMedia = DataOfMedia(),
    val album: DataOfAlbum = DataOfAlbum(),
    val mediaByAlbum: DataOfMediaByAlbum = DataOfMediaByAlbum()
)