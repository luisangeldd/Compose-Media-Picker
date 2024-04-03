package api.luisangeldd.mediapicker.data.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
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
import api.luisangeldd.mediapicker.data.model.AlbumData
import api.luisangeldd.mediapicker.data.model.Media
import api.luisangeldd.mediapicker.data.model.MediaData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

internal class MediaPickerRepoImpl(private val context: Context) : MediaPickerRepo {
    init {
        CoroutineScope(Dispatchers.IO).launch{
            if (selectionMediaImage.isBlank()){
                selectionMediaImage()
            }
            if (selectionMediaVideo.isBlank()){
                selectionMediaVideo()
            }
        }
    }
    override suspend fun fetchMedia(): List<MediaData> {
        return withContext(Dispatchers.IO) {
            try{
                val imageList = queryUri(
                    context.contentResolver,
                    Images.Media.EXTERNAL_CONTENT_URI,
                    selectionMediaImage.toString(),
                    selectionArgsMediaImage.toTypedArray(),
                    SORT_ORDER_PICTURES
                ).use {
                    it?.getMediaPicturesFromCursor() ?: listOf()
                }
                val videoList = queryUri(
                    context.contentResolver,
                    Video.Media.EXTERNAL_CONTENT_URI,
                    selectionMediaVideo.toString(),
                    selectionArgsMediaVideo.toTypedArray(),
                    SORT_ORDER_VIDEOS
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
    @Suppress("UNCHECKED_CAST")
    override suspend fun fetchAlbums(files: List<MediaData>): List<AlbumData> = withContext(Dispatchers.IO) {
        try{
            val resultMap = mutableMapOf<File, MutableList<MediaData>>()
            for (file in files.asReversed()) {
                if(file.fileMedia.totalSpace != 0L){
                    (!resultMap.containsKey(file.fileMedia.parentFile!!)).let { resultMap.put(file.fileMedia.parentFile!!, mutableListOf()) }
                    resultMap[file.fileMedia.parentFile!!]?.add(file)
                }
            }
            val folders: HashMap<File, List<MediaData>> = resultMap as HashMap<File, List<MediaData>>
            val indexes = folders.keys.toList().sortedBy { it.nameWithoutExtension }
            val media = mutableListOf<AlbumData>()
            indexes.forEach { flag ->
                media.add(
                    AlbumData(
                        itemsFolder = getItemsFromFolder(flag),
                        pathFromFolder = flag,
                        uri = folders[flag]!![0].uriMedia,
                        id = folders[flag]!![0].idMedia,
                        mimeType = folders[flag]!![0].mimeType
                    )
                )
            }
            media
        }catch (e:Exception){
            emptyList()
        }
    }
    override suspend fun fetchMediaByAlbum(folder: String): List<MediaData> = withContext(Dispatchers.IO) {
        try{
            val picturesList = queryUri(
                context.contentResolver,
                Images.Media.EXTERNAL_CONTENT_URI,
                Images.Media.DATA + " LIKE ? AND " + Images.Media.DATA  + " NOT LIKE ? AND " + selectionMediaImage.toString(),
                arrayOf(
                    "%$folder%",
                    "%$folder/%/%"
                ).plus(selectionArgsMediaImage.toTypedArray()),
                SORT_ORDER_PICTURES
            ).use { it?.getMediaPicturesFromCursor() ?: listOf() }
            val videoList = queryUri(
                context.contentResolver,
                Video.Media.EXTERNAL_CONTENT_URI,
                Video.Media.DATA + " LIKE ? AND " + Video.Media.DATA + " NOT LIKE ? AND " + selectionMediaVideo.toString(),
                arrayOf(
                    "%$folder%",
                    "%$folder/%/%"
                ).plus(selectionArgsMediaVideo.toTypedArray()),
                SORT_ORDER_VIDEOS
            ).use { it?.getMediaVideosFromCursor() ?: listOf() }
            (picturesList + videoList).sortedWith(compareByDescending { it.fileMedia.lastModified() })
        }catch (e:Exception){
            emptyList()
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
        private val fileExtensions = IMAGE_EXTENSIONS_SUPPORT.plus(VIDEO_EXTENSIONS_SUPPORT)
        private const val SORT_ORDER_PICTURES = Images.Media.DATE_TAKEN + " DESC"
        private const val SORT_ORDER_VIDEOS = Video.Media.DATE_TAKEN + " DESC"
        private val projection = arrayOf(
            Files.FileColumns._ID,
            Files.FileColumns.DATA,
            Files.FileColumns.DATE_ADDED,
            Files.FileColumns.MIME_TYPE,
            Files.FileColumns.TITLE
        )
        private suspend fun queryUri(
            contentResolver: ContentResolver,
            uri: Uri,
            selection: String?,
            selectionArgs: Array<String>?,
            sortOrder: String? = ""
        ): Cursor? = withContext(Dispatchers.IO) {
            contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
        }
        private fun Cursor.getMediaPicturesFromCursor(): List<MediaData> {
            val mediaPictures = mutableListOf<MediaData>()
            while (this.moveToNext()) {
                val mime = this.getString(this.getColumnIndexOrThrow(Images.Media.MIME_TYPE))
                val id = this.getLong(this.getColumnIndexOrThrow(Images.Media._ID))
                val uri = ContentUris.withAppendedId(
                    Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                mediaPictures.add(
                    MediaData(id,uri,File(this.getString(this.getColumnIndexOrThrow(MediaColumns.DATA))),mime)
                )
            }
            return mediaPictures
        }
        private fun Cursor.getMediaVideosFromCursor(): List<MediaData> {
            val mediaVideos = mutableListOf<MediaData>()
            while (this.moveToNext()) {
                val mime = this.getString(this.getColumnIndexOrThrow(Video.Media.MIME_TYPE))
                val id = this.getLong(this.getColumnIndexOrThrow(Video.Media._ID))
                val uri = ContentUris.withAppendedId(
                    Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                mediaVideos.add(
                    MediaData(id,uri,File(this.getString(this.getColumnIndexOrThrow(MediaColumns.DATA))),mime)
                )
            }
            return mediaVideos
        }
        private fun getItemsFromFolder(parent : File) : Int {
            var items = 0
            parent.listFiles()!!.forEach {
                when (it) {
                    is File -> {
                        if (fileExtensions.contains(it.extension.lowercase())) {
                            items++
                        }
                    }
                    else -> {}
                }
            }
            return items
        }
        private suspend fun getBrokenThumbnail(resources: Resources, width: Int, height: Int): Bitmap = withContext(Dispatchers.IO) {
            resources.getDrawable(R.drawable.broken_media, null).toBitmap(width, height)
        }
        @SuppressLint("NewApi")
        @Suppress("DEPRECATION")
        private suspend fun fetchThumbnail(
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
                bmp ?: getBrokenThumbnail(context.resources, resolution, resolution)
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
