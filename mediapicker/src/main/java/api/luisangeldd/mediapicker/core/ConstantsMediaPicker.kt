package api.luisangeldd.mediapicker.core

internal object ConstantsMediaPicker {
    const val MIME_IMAGE = "image"
    const val MIME_VIDEO = "video"
    val IMAGE_EXTENSIONS_SUPPORT = arrayOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    val VIDEO_EXTENSIONS_SUPPORT = arrayOf("mp4", "mkv", "avi", "wmv", "mov", "webm")
    const val FOLDERS = "folders"
    const val FOLDER_CONTENT = "folders_content"
    const val FOLDER_NAME = "folder_name"
    infix fun String.arg(arg: String) = "$this/{$arg}"
    infix fun String.argSend(arg: String) = "$this/$arg"
}