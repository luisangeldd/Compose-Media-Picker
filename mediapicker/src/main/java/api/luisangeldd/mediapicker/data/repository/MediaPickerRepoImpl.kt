package api.luisangeldd.mediapicker.data.repository

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Video
import android.provider.MediaStore.Images
import android.provider.MediaStore.Files
import android.provider.MediaStore.MediaColumns
import android.util.Size
import androidx.core.graphics.drawable.toBitmap
import api.luisangeldd.mediapicker.R
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.IMAGE_EXTENSIONS_SUPPORT
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.MIME_IMAGE
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.MIME_VIDEO
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.VIDEO_EXTENSIONS_SUPPORT
import api.luisangeldd.mediapicker.data.model.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class MediaPickerRepoImpl(private val context: Context) : MediaPickerRepo {
    override suspend fun fetchMedia(): List<Media> {
        return withContext(Dispatchers.IO) {
            try{
                selectionMediaImage()
                selectionMediaVideo()
                val sortOrderPictures = Images.Media.DATE_TAKEN + " DESC"
                val sortOrderVideos = Video.Media.DATE_TAKEN + " DESC"
                val imageList = queryUri(
                    context,
                    Images.Media.EXTERNAL_CONTENT_URI,
                    selectionMediaImage.toString(),
                    selectionArgsMediaImage.toTypedArray(),
                    sortOrderPictures
                ).use {
                    it?.getMediaPicturesFromCursor() ?: listOf()
                }
                val videoList = queryUri(
                    context,
                    Video.Media.EXTERNAL_CONTENT_URI,
                    selectionMediaVideo.toString(),
                    selectionArgsMediaVideo.toTypedArray(),
                    sortOrderVideos
                ).use {
                    it?.getMediaVideosFromCursor() ?: listOf()
                }
                (imageList + videoList).sortedWith(
                    compareByDescending {
                        it.fileMedia.lastModified()
                    }
                )
            }catch (e:Exception){
                emptyList()
            }
        }
    }

    override suspend fun fetchThumbnail(uri : Uri, id : Long, mimeType : String, resolutionHeight: Boolean) = withContext(Dispatchers.IO) {
        fetchThumbnail(context, uri, id, mimeType,resolutionHeight)
    }

    companion object {
        private val selectionMediaImage = StringBuilder("")
        private val selectionArgsMediaImage: MutableList<String> = ArrayList()
        private val selectionMediaVideo = StringBuilder("")
        private val selectionArgsMediaVideo: MutableList<String> = ArrayList()
        private val projection = arrayOf(
            Files.FileColumns._ID,
            Files.FileColumns.DATA,
            Files.FileColumns.DATE_ADDED,
            Files.FileColumns.MIME_TYPE,
            Files.FileColumns.TITLE
        )
        private fun queryUri(context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?, sortOrder: String? = ""): Cursor? {
            return context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder)
        }
        private fun Cursor.getMediaPicturesFromCursor(): List<Media> {
            val mediaPictures = mutableListOf<Media>()
            while (this.moveToNext()) {
                val mime = this.getString(this.getColumnIndexOrThrow(Images.Media.MIME_TYPE))
                val id = this.getLong(this.getColumnIndexOrThrow(Images.Media._ID))
                val uri = ContentUris.withAppendedId(
                    Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                mediaPictures.add(
                    Media(id,uri,File(this.getString(this.getColumnIndexOrThrow(MediaColumns.DATA))),mime)
                )
            }
            return mediaPictures
        }
        private fun Cursor.getMediaVideosFromCursor(): List<Media> {
            val mediaVideos = mutableListOf<Media>()
            while (this.moveToNext()) {
                val mime = this.getString(this.getColumnIndexOrThrow(Video.Media.MIME_TYPE))
                val id = this.getLong(this.getColumnIndexOrThrow(Video.Media._ID))
                val uri = ContentUris.withAppendedId(
                    Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                mediaVideos.add(
                    Media(id,uri,File(this.getString(this.getColumnIndexOrThrow(MediaColumns.DATA))),mime)
                )
            }
            return mediaVideos
        }
        @SuppressLint("UseCompatLoadingForDrawables")
        private fun getBrokenThumbnail(context: Context, width: Int, height: Int): Bitmap {
            return context.resources.getDrawable(R.drawable.broken_media, null).toBitmap(width, height)
        }
        @SuppressLint("NewApi")
        @Suppress("DEPRECATION")
        private fun fetchThumbnail(
            context: Context,
            uri : Uri,
            id : Long,
            mimeType : String,
            resolutionHeight: Boolean
        ) : Bitmap = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                val resolution = when (resolutionHeight) {
                    true -> {
                        listOf(512,384)
                    }
                    false -> {
                        listOf(96,96)
                    }
                }
                context.contentResolver.loadThumbnail(
                    uri,
                    Size(resolution[0], resolution[1]),
                    null
                )
            }
            else -> {
                val resolution = when (resolutionHeight) {
                    true -> {
                        1
                    }
                    false -> {
                        3
                    }
                }
                val bmp = when (mimeType.split('/')[0]){
                    MIME_IMAGE -> {
                        Images.Thumbnails.getThumbnail(
                            context.contentResolver,
                            id,
                            resolution,
                            null
                        )
                    }
                    MIME_VIDEO -> {
                        Video.Thumbnails.getThumbnail(
                            context.contentResolver,
                            id,
                            resolution,
                            null
                        )
                    }
                    else -> {
                        null
                    }
                }
                bmp ?: getBrokenThumbnail(context, resolution, resolution)
            }
        }

        private fun selectionMediaImage() {
            if (IMAGE_EXTENSIONS_SUPPORT.isNotEmpty()) {
                selectionMediaImage.append(Images.Media.MIME_TYPE + " IN (")
                for (i in IMAGE_EXTENSIONS_SUPPORT.indices) {
                    selectionMediaImage.append("?,")
                    selectionArgsMediaImage.add(MIME_IMAGE+"/"+IMAGE_EXTENSIONS_SUPPORT[i])
                }
                selectionMediaImage.replace(selectionMediaImage.length - 1, selectionMediaImage.length, ")")
            }
        }
        private fun selectionMediaVideo() {
            if (VIDEO_EXTENSIONS_SUPPORT.isNotEmpty()) {
                selectionMediaVideo.append(Video.Media.MIME_TYPE + " IN (")
                for (i in VIDEO_EXTENSIONS_SUPPORT.indices) {
                    selectionMediaVideo.append("?,")
                    selectionArgsMediaVideo.add(MIME_VIDEO+"/"+VIDEO_EXTENSIONS_SUPPORT[i])
                }
                selectionMediaVideo.replace(selectionMediaVideo.length - 1, selectionMediaVideo.length, ")")
            }
        }
    }
}
