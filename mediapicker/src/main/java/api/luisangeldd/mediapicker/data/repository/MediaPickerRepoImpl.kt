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
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.imageExtensionsSupport
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.mimeImage
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.mimeVideo
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.videoExtensionsSupport
import api.luisangeldd.mediapicker.data.model.Media
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
        @Suppress("DEPRECATION")
        private fun fetchThumbnail(context: Context,uri : Uri, id : Long, mimeType : String) : Bitmap {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(uri, Size(640, 480), null)
            } else {
                val bmp = when (mimeType.split('/')[0]){
                    mimeImage -> {
                        Images.Thumbnails.getThumbnail(context.contentResolver, id, Images.Thumbnails.MINI_KIND, null)
                    }
                    mimeVideo -> {
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
            if (imageExtensionsSupport.isNotEmpty()) {
                selectionMediaImage.append(Images.Media.MIME_TYPE + " IN (")
                for (i in imageExtensionsSupport.indices) {
                    selectionMediaImage.append("?,")
                    selectionArgsMediaImage.add(mimeImage+"/"+imageExtensionsSupport[i])
                }
                selectionMediaImage.replace(selectionMediaImage.length - 1, selectionMediaImage.length, ")")
            }
        }
        private fun selectionMediaVideo() {
            if (videoExtensionsSupport.isNotEmpty()) {
                selectionMediaVideo.append(Video.Media.MIME_TYPE + " IN (")
                for (i in videoExtensionsSupport.indices) {
                    selectionMediaVideo.append("?,")
                    selectionArgsMediaVideo.add(mimeVideo+"/"+videoExtensionsSupport[i])
                }
                selectionMediaVideo.replace(selectionMediaVideo.length - 1, selectionMediaVideo.length, ")")
            }
        }
    }
}
