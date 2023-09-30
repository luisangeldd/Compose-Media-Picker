package api.luisangeldd.mediapicker.h.data.repository

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
import api.luisangeldd.mediapicker.h.R
import api.luisangeldd.mediapicker.h.data.model.Media
import api.luisangeldd.mediapicker.h.utils.IMAGE
import api.luisangeldd.mediapicker.h.utils.VIDEO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MediaPickerRepoImpl(private val context: Context) : MediaPickerRepo {
    override suspend fun fetchMedia(): List<Media> {
        return withContext(Dispatchers.IO) {
            try{
                selectionMediaImage()
                selectionMediaVideo()
                val sortOrderPictures = Images.Media.DATE_TAKEN + " DESC"
                val sortOrderVideos = Video.Media.DATE_TAKEN + " DESC"
                val imageList = queryUri(context, Images.Media.EXTERNAL_CONTENT_URI, selectionMediaImage.toString(), selectionArgsMediaImage.toTypedArray(), sortOrderPictures)
                    .use { it?.getMediaPicturesFromCursor() ?: listOf() }
                val videoList = queryUri(context, Video.Media.EXTERNAL_CONTENT_URI, selectionMediaVideo.toString(), selectionArgsMediaVideo.toTypedArray(), sortOrderVideos)
                    .use { it?.getMediaVideosFromCursor() ?: listOf() }
                (imageList + videoList).sortedWith(compareByDescending { it.fileMedia.lastModified() })
            }catch (e:Exception){
                emptyList()
            }
        }
    }

    override fun fetchThumbnail(uri : Uri, id : Long, mimeType : String) = fetchThumbnail(context, uri, id, mimeType)

    companion object {
        private val selectionMediaImage = StringBuilder("")
        private val selectionArgsMediaImage: MutableList<String> = ArrayList()
        private val selectionMediaVideo = StringBuilder("")
        private val selectionArgsMediaVideo: MutableList<String> = ArrayList()
        private val imageExtensions = arrayOf("$IMAGE/jpg", "$IMAGE/jpeg", "$IMAGE/png", "$IMAGE/gif", "$IMAGE/bmp", "$IMAGE/webp")
        private val videoExtensions = arrayOf("$VIDEO/mp4", "$VIDEO/mkv", "$VIDEO/avi", "$VIDEO/wmv", "$VIDEO/mov", "$VIDEO/webm")
        private val fileExtensions = imageExtensions.plus(videoExtensions)
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
        private fun getBrokenThumbnail(context: Context, width: Int, height: Int): Bitmap {
            return context.resources.getDrawable(R.drawable.broken_media, null).toBitmap(width, height)
        }
        @Suppress("DEPRECATION")
        private fun fetchThumbnail(context: Context,uri : Uri, id : Long, mimeType : String) : Bitmap {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(uri, Size(640, 480), null)
            } else {
                val bmp = when (mimeType.split('/')[0]){
                    IMAGE -> {
                        Images.Thumbnails.getThumbnail(context.contentResolver, id, Images.Thumbnails.MINI_KIND, null)
                    }
                    VIDEO -> {
                        Video.Thumbnails.getThumbnail(context.contentResolver, id, Images.Thumbnails.MINI_KIND, null)
                    }
                    else -> {
                        null
                    }
                }
                bmp ?: getBrokenThumbnail(context, 640, 480)
            }
        }
        private fun selectionMediaImage() {
            if (imageExtensions.isNotEmpty()) {
                selectionMediaImage.append(Images.Media.MIME_TYPE + " IN (")
                for (i in imageExtensions.indices) {
                    selectionMediaImage.append("?,")
                    selectionArgsMediaImage.add(imageExtensions[i])
                }
                selectionMediaImage.replace(selectionMediaImage.length - 1, selectionMediaImage.length, ")")
            }
        }
        private fun selectionMediaVideo() {
            if (videoExtensions.isNotEmpty()) {
                selectionMediaVideo.append(Video.Media.MIME_TYPE + " IN (")
                for (i in videoExtensions.indices) {
                    selectionMediaVideo.append("?,")
                    selectionArgsMediaVideo.add(videoExtensions[i])
                }
                selectionMediaVideo.replace(selectionMediaVideo.length - 1, selectionMediaVideo.length, ")")
            }
        }
    }
}
