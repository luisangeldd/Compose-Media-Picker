package api.luisangeldd.mediapicker.data

import android.net.Uri
import api.luisangeldd.mediapicker.data.model.MediaData
import api.luisangeldd.mediapicker.data.repository.MediaPickerRepo

internal class MediaPickerUseCase(private val mediaPickerRepo: MediaPickerRepo) {
    suspend fun fetchMedia() = mediaPickerRepo.fetchMedia()
    suspend fun fetchAlbums(files: List<MediaData>) = mediaPickerRepo.fetchAlbums(files)
    suspend fun fetchMediaByAlbum(folder: String) = mediaPickerRepo.fetchMediaByAlbum(folder)
    suspend fun fetchThumbnail(
        uri : Uri,
        id : Long,
        mimeType : String,
        resolutionHeight: Boolean
    ) = mediaPickerRepo.fetchThumbnail(
        uri,
        id,
        mimeType,
        resolutionHeight
    )
}