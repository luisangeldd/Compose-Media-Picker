package api.luisangeldd.mediapicker.ui

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import api.luisangeldd.mediapicker.core.AnswerOfRequest
import api.luisangeldd.mediapicker.core.StateOfRequest
import api.luisangeldd.mediapicker.core.StatePicker
import api.luisangeldd.mediapicker.data.model.MediaUser
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import kotlinx.coroutines.launch

/**
 *[Media Picker](https://github.com/luisangeldd/MediaPicker).
 *
 * Media picker is a powerful api for Jetpack Compose with Kotlin to get media content from any device.
 *
 * The media picker is used as an alternative to the default file manager of the mobile device, this API is inspired by the
 * [Photo picker](https://developer.android.com/training/data-storage/shared/photopicker?hl=es-419) API, it provides the device
 * content such as images and videos, retrieves the file bitmaps and the displays in a LazyGrid that allows you to select them
 * and display the selected content in a Carousel, in addition to remembering the selected elements to select new ones or remove
 * previously selected elements. Like dialog boxes and modal bottom sheets, the media switcher appears in front of the application
 * content, disables all other functions of the application when it appears, and remains on the screen until it is confirmed,
 * dismissed, or executed. a required action.
 *
 * A simple example of a media picker looks like this:
 *
 * @param actionStart returns the trigger action to trigger the window which is activated when storage permissions have been granted.
 * @param multiMedia set if use to select a single o multi media items, true for multi medias and false for single selection.
 * @param showCarousel set if use to show the carousel of media selected, it is "true" for the fault you can change to "false" if you don't show and use a own implementation.
 * @param getMedia returns a list of Media type objects which allows recovering the Uri and File of the selected files.
 * @param removeItem returns a function to remove a item at getMedia model return.
 */
@Composable
fun MediaPicker(
    actionStart: (() -> Unit) -> Unit,
    multiMedia: Boolean = true,
    showCarousel: Boolean = true,
    getMedia: suspend (List<MediaUser>) -> Unit,
    removeItem: ((Int) -> Unit) -> Unit
){
    val context = LocalContext.current
    val viewModelMediaPicker = viewModel<ViewModelMediaPicker>(
        factory = viewModelFactory {
            initializer {
                ViewModelMediaPicker(context = context)
            }
        }
    )
    actionStart { viewModelMediaPicker.statePicker(StatePicker.OPEN) }
    MediaPickerStart(
        viewModelMediaPicker = viewModelMediaPicker,
        multiMedia = multiMedia,
        showCarousel = showCarousel,
        setMediaCollect = getMedia,
        removeItem = removeItem
    )
}
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun MediaPickerStart(
    viewModelMediaPicker: ViewModelMediaPicker,
    multiMedia: Boolean,
    showCarousel: Boolean,
    setMediaCollect: suspend (List<MediaUser>) -> Unit,
    removeItem: ((Int)-> Unit) -> Unit
){
    val statePicker by viewModelMediaPicker.statePicker.collectAsState()
    val media by viewModelMediaPicker.dataOfMedia.collectAsState()
    val mediaSelected by viewModelMediaPicker.mediaSelected.collectAsState()
    val scope = rememberCoroutineScope()
    val stateLazyGridMedia = rememberLazyGridState()
    val stateLazyGridAlbum = rememberLazyGridState()
    val stateLazyGridMediaByAlbum = rememberLazyGridState()
    val selectedPager = rememberSaveable { mutableStateOf(true) }
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f) { 2 }
    val isSelectedMode = rememberSaveable { mutableStateOf(false) }
    val isScrollingMedia = remember { mutableStateOf(false) }
    val isScrollingFolder = remember { mutableStateOf(false) }
    val isScrollingMediaByFolder = remember { mutableStateOf(false) }
    val index: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
    val indexAux: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val (openAlertDialog,onOpenAlertDialog) = rememberSaveable { mutableStateOf(false) }
    val showFabMedia by remember(isScrollingMedia.value) {
        mutableStateOf(isScrollingMedia.value)
    }
    val showFabFolder by remember(isScrollingFolder.value) {
        mutableStateOf(isScrollingFolder.value)
    }
    val showFabMediaByFolder by remember(isScrollingMediaByFolder.value) {
        mutableStateOf(isScrollingMediaByFolder.value)
    }
    LaunchedEffect(key1 = mediaSelected, block = {
        setMediaCollect(
            mediaSelected.map {
                MediaUser(item = it.item, uriMedia = it.media.uriMedia, fileMedia = it.media.fileMedia)
            }
        )
    })
    removeItem {
        scope.launch {
            index.value -= it
            viewModelMediaPicker.setMedia(index.value.map { MediaUserV0(item = it, media = media.media.media[it]) })
        }
    }
    if (mediaSelected.isNotEmpty()){
        if (showCarousel) {
            MediaCarousel(
                mediaSelected = mediaSelected,
                removeItem = {
                    scope.launch {
                        index.value -= it
                        viewModelMediaPicker.setMedia(index.value.map { MediaUserV0(item = it,media = media.media.media[it]) })
                    }
                }
            )
        }
    }
    if (statePicker == StatePicker.OPEN) {
        LayoutOfMediaPicker(
            onDismissRequest = { viewModelMediaPicker.statePicker(StatePicker.DRAG) },
            sheetState = bottomSheetState,
            topAppBarFromMediaPicker = {
                TopAppBarMediaPicker(
                    selectedPager = selectedPager.value,
                    goToPhoto = {
                        if (!it){
                            scope.launch { pagerState.scrollToPage(pagerState.currentPage-1) }
                        }
                    },
                    goToAlbum = {
                        if (!it){
                            scope.launch { pagerState.scrollToPage(pagerState.currentPage+1) }
                        }
                    },
                    closeMediaPicker = { viewModelMediaPicker.statePicker(StatePicker.CLOSE) }
                )
            },
            contentFromMediaPicker = {
                ViewOfMedia(
                    state = pagerState,
                    contentPage = {
                        when(media.media.stateOfRequestMedia){
                            StateOfRequest.IDLE, StateOfRequest.START -> {
                                if (media.media.stateOfRequestMedia == StateOfRequest.IDLE){
                                    viewModelMediaPicker.getMedia()
                                }
                                GridOfMediaLoad(it)
                            }
                            StateOfRequest.END -> {
                                when (it) {
                                    0 -> {
                                        ContentOfMedia(
                                            contentFromMediaPicker = { pdd ->
                                                when(media.media.answerOfRequestMedia){
                                                    AnswerOfRequest.IDLE, AnswerOfRequest.EMPTY  -> {}
                                                    AnswerOfRequest.NOT_EMPTY-> {
                                                        GridOfMediaLoaded(
                                                            paddingValues = pdd,
                                                            multiMedia = multiMedia,
                                                            thumbnail = viewModelMediaPicker::getThumbnail,
                                                            media = media.media.media,
                                                            isSelectedMode = isSelectedMode,
                                                            itemsSelected = { data -> index.value = data},
                                                            stateLazyGridPhoto = stateLazyGridMedia,
                                                            isScrolling = isScrollingMedia,
                                                            userScrollEnabled = true,
                                                            selectedIds = index
                                                        )
                                                    }
                                                }
                                            },
                                            bottomAppBarFromMediaPicker = {
                                                if (isSelectedMode.value){
                                                    BottomAppBarMediaPicker(
                                                        addItems = {
                                                            viewModelMediaPicker.statePicker(StatePicker.ADD)
                                                        },
                                                        removeItem ={
                                                            if (multiMedia){
                                                                if (index.value.isNotEmpty()) {
                                                                    IconButton(onClick = { onOpenAlertDialog(true) }) {
                                                                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        goToTop = {
                                                            androidx.compose.animation.AnimatedVisibility(
                                                                visible = showFabMedia,
                                                                enter = slideInVertically { it * 2 },
                                                                exit = slideOutVertically { it * 2 },
                                                                content = {
                                                                    FloatingActionButton(
                                                                        onClick = {
                                                                            scope.launch{
                                                                                stateLazyGridMedia.animateScrollToItem(0)
                                                                            }
                                                                        }
                                                                    ) {
                                                                        Icon(imageVector = Icons.Rounded.ExpandLess,contentDescription = null)
                                                                    }
                                                                }
                                                            )
                                                        },
                                                        items = if (multiMedia) "${if (index.value.size > 99) "99+" else index.value.size}" else "1"
                                                    )
                                                }
                                            },
                                            floatingActionButtonFromMediaPicker = {
                                                if (!isSelectedMode.value){
                                                    androidx.compose.animation.AnimatedVisibility(
                                                        visible = showFabMedia,
                                                        enter = slideInVertically { it * 2 },
                                                        exit = slideOutVertically { it * 2 },
                                                        content = {
                                                            FloatingActionButton(
                                                                onClick = {
                                                                    scope.launch{
                                                                        stateLazyGridMedia.animateScrollToItem(0)
                                                                    }
                                                                }
                                                            ) {
                                                                Icon(imageVector = Icons.Rounded.ExpandLess,contentDescription = null)
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        )
                                    }
                                    1 -> {
                                        MediaByFolder(
                                            albums = { pdd, goToPhotoByAlbum ->
                                                when(media.album.stateOfRequestAlbum){
                                                    StateOfRequest.IDLE, StateOfRequest.START -> {
                                                        if (media.album.stateOfRequestAlbum == StateOfRequest.IDLE){
                                                            viewModelMediaPicker.getAlbums()
                                                        }
                                                        GridOfMediaLoad(1)
                                                    }
                                                    StateOfRequest.END -> {
                                                        when(media.album.answerOfRequestAlbum){
                                                            AnswerOfRequest.IDLE, AnswerOfRequest.EMPTY  -> {}
                                                            AnswerOfRequest.NOT_EMPTY-> {
                                                                GridOfFoldersLoaded(
                                                                    paddingValues = pdd,
                                                                    dataFolder = media.album.album,
                                                                    stateLazyGridAlbum = stateLazyGridAlbum,
                                                                    thumbnail = viewModelMediaPicker::getThumbnail,
                                                                    getItemsByFolder = { route, folderName ->
                                                                        viewModelMediaPicker.getMediaByAlbum(route)
                                                                        goToPhotoByAlbum(folderName)
                                                                    },
                                                                    isScrolling = isScrollingFolder
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            mediaByAlbum = { pdd ->
                                                when(media.mediaByAlbum.stateOfRequestMediaByAlbum){
                                                    StateOfRequest.IDLE, StateOfRequest.START -> {
                                                        if (media.mediaByAlbum.stateOfRequestMediaByAlbum == StateOfRequest.IDLE){
                                                            viewModelMediaPicker.getAlbums()
                                                        }
                                                        GridOfMediaLoad(0)
                                                    }
                                                    StateOfRequest.END -> {
                                                        when(media.mediaByAlbum.answerOfRequestMediaByAlbum){
                                                            AnswerOfRequest.IDLE, AnswerOfRequest.EMPTY  -> {}
                                                            AnswerOfRequest.NOT_EMPTY-> {
                                                                GridOfMediaLoaded(
                                                                    paddingValues = pdd,
                                                                    multiMedia = multiMedia,
                                                                    thumbnail = viewModelMediaPicker::getThumbnail,
                                                                    media = media.mediaByAlbum.mediaByAlbum,
                                                                    stateLazyGridPhoto = stateLazyGridMediaByAlbum,
                                                                    isSelectedMode = isSelectedMode,
                                                                    itemsSelected = { data -> index.value = data},
                                                                    isScrolling = isScrollingMediaByFolder,
                                                                    userScrollEnabled = true,
                                                                    selectedIds = index
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            bottomAppBarFromMediaPickerToMediaByAlbum = {
                                                if (isSelectedMode.value){
                                                    BottomAppBarMediaPicker(
                                                        addItems = {
                                                            viewModelMediaPicker.statePicker(StatePicker.ADD)
                                                        },
                                                        removeItem ={
                                                            if (multiMedia){
                                                                if (index.value.isNotEmpty()) {
                                                                    IconButton(onClick = { onOpenAlertDialog(true) }) {
                                                                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        goToTop = {
                                                            androidx.compose.animation.AnimatedVisibility(
                                                                visible = showFabMediaByFolder,
                                                                enter = slideInVertically { it * 2 },
                                                                exit = slideOutVertically { it * 2 },
                                                                content = {
                                                                    FloatingActionButton(
                                                                        onClick = {
                                                                            scope.launch{
                                                                                stateLazyGridMediaByAlbum.animateScrollToItem(0)
                                                                            }
                                                                        }
                                                                    ) {
                                                                        Icon(imageVector = Icons.Rounded.ExpandLess,contentDescription = null)
                                                                    }
                                                                }
                                                            )
                                                        },
                                                        items = if (multiMedia) "(${index.value.size})" else ""
                                                    )
                                                }
                                            },
                                            floatingActionButtonFromMediaPickerToAlbums = {
                                                androidx.compose.animation.AnimatedVisibility(
                                                    visible = showFabFolder,
                                                    enter = slideInVertically { it * 2 },
                                                    exit = slideOutVertically { it * 2 },
                                                    content = {
                                                        FloatingActionButton(
                                                            onClick = {
                                                                scope.launch{
                                                                    stateLazyGridAlbum.animateScrollToItem(0)
                                                                }
                                                            }
                                                        ) {
                                                            Icon(imageVector = Icons.Rounded.ExpandLess,contentDescription = null)
                                                        }
                                                    }
                                                )
                                            },
                                            floatingActionButtonFromMediaPickerToMediaByAlbum = {
                                                if (!isSelectedMode.value){
                                                    androidx.compose.animation.AnimatedVisibility(
                                                        visible = showFabMediaByFolder,
                                                        enter = slideInVertically { it * 2 },
                                                        exit = slideOutVertically { it * 2 },
                                                        content = {
                                                            FloatingActionButton(
                                                                onClick = {
                                                                    scope.launch{
                                                                        stateLazyGridMediaByAlbum.animateScrollToItem(0)
                                                                    }
                                                                }
                                                            ) {
                                                                Icon(imageVector = Icons.Rounded.ExpandLess,contentDescription = null)
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        selectedPager.value = it == 0
                    },
                )
            }
        )
    }
    if (openAlertDialog){
        MessageClearSelection(
            onOpenAlertDialog = onOpenAlertDialog,
            index = index,
            setMediaCollect = viewModelMediaPicker::setMedia
        )
    }
    LaunchedEffect(key1 = statePicker, block = {
        when (statePicker) {
            StatePicker.OPEN -> {
                indexAux.value = index.value
            }
            else -> {
                bottomSheetState.hide()
                when (statePicker) {
                    StatePicker.DRAG, StatePicker.CLOSE -> {
                        if (mediaSelected.isEmpty()) {
                            index.value = emptySet()
                        } else {
                            if (index.value != indexAux.value) {
                                index.value = indexAux.value
                            }
                        }
                    }
                    StatePicker.ADD -> {
                        viewModelMediaPicker.setMedia(index.value.map { MediaUserV0(item = it, media = media.media.media[it]) })
                    }
                    else -> {}
                }
                viewModelMediaPicker.clearMedia()
                if (!selectedPager.value) {
                    if (pagerState.currentPage-1 >=0) { pagerState.scrollToPage(pagerState.currentPage-1) }
                }
                stateLazyGridMedia.animateScrollToItem(0)
            }
        }
    })
}