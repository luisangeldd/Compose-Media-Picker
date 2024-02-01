package api.luisangeldd.mediapicker.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.util.lerp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import api.luisangeldd.mediapicker.R
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.MIME_IMAGE
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.MIME_VIDEO
import api.luisangeldd.mediapicker.core.StatePicker
import api.luisangeldd.mediapicker.core.StateRequest
import api.luisangeldd.mediapicker.core.StatusRequest
import api.luisangeldd.mediapicker.data.model.Media
import api.luisangeldd.mediapicker.data.model.MediaUser
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

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
 */
@Composable
fun MediaPicker(
    actionStart: (() -> Unit) -> Unit,
    multiMedia: Boolean = true,
    showCarousel: Boolean = true,
    getMedia: (List<MediaUser>) -> Unit,
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
    val statePicker by viewModelMediaPicker.statePicker.collectAsState()
    val media by viewModelMediaPicker.media.collectAsState()
    val stateRequestMedia by viewModelMediaPicker.stateRequestMedia.collectAsState()
    val statusRequestMedia by viewModelMediaPicker.statusRequestMedia.collectAsState()
    val mediaSelected by viewModelMediaPicker.mediaSelected.collectAsState()

    MediaPickerStart(
        multiMedia = multiMedia,
        showCarousel = showCarousel,
        statePicker = statePicker,
        media = media,
        stateRequestMedia = stateRequestMedia,
        statusRequestMedia = statusRequestMedia,
        mediaSelected = mediaSelected,
        getThumbnail = viewModelMediaPicker::getThumbnail,
        setMedia = viewModelMediaPicker::setMedia,
        setStatePicker = viewModelMediaPicker::statePicker,
        setMediaCollect = getMedia,
        getMedia = viewModelMediaPicker::getMedia,
        removeItem = removeItem
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaPickerStart(
    multiMedia: Boolean,
    showCarousel: Boolean,
    statePicker: StatePicker,
    media: List<Media>,
    stateRequestMedia: StateRequest,
    statusRequestMedia: StatusRequest,
    mediaSelected: List<MediaUserV0>,
    getThumbnail: suspend (Uri, Long, String) -> Bitmap?,
    setMedia: (List<MediaUserV0>) -> Unit,
    setStatePicker:(StatePicker) -> Unit,
    setMediaCollect: (List<MediaUser>) -> Unit,
    getMedia: () -> Unit,
    removeItem: ((Int)-> Unit) -> Unit
){
    val scope = rememberCoroutineScope()
    val state = rememberLazyGridState()
    val (isSelectedMode, onSelectedMode) = rememberSaveable { mutableStateOf(false) }
    val index: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
    val indexAux: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val (openAlertDialog,onOpenAlertDialog) = rememberSaveable { mutableStateOf(false) }
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
            setMedia(index.value.map { MediaUserV0(item = it, media = media[it]) })
        }
    }
    if (mediaSelected.isNotEmpty()){
        if (showCarousel) {
            MediaCarousel(
                media = mediaSelected,
                thumbnail = getThumbnail,
                removeItem = {
                    scope.launch {
                        index.value -= it
                        setMedia(index.value.map { MediaUserV0(item = it,media = media[it]) })
                    }
                }
            )
        }
    }
    if (statePicker == StatePicker.OPEN) {
        ModalBottomSheet(
            onDismissRequest = {setStatePicker(StatePicker.DRAG) },
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(0.dp)
        ) {
            Scaffold(
                topBar = {
                    TopBarMediaViewer(
                        multiMedia = multiMedia,
                        title = "Media",
                        navIcon = {
                            setStatePicker(StatePicker.CLOSE)
                        },
                        action = {
                            onOpenAlertDialog(true)
                        },
                        isNotEmpty = index.value.isNotEmpty()
                    )
                },
                content = { paddingInter ->
                    Box(Modifier.padding(paddingInter)){
                        when(stateRequestMedia){
                            StateRequest.IDLE -> {
                                if(media.isEmpty()) getMedia()
                            }
                            StateRequest.START -> {
                                GridOfMediaThumbnailLoad()
                            }
                            StateRequest.END -> {
                                when(statusRequestMedia){
                                    StatusRequest.IDLE -> {
                                        GridOfMediaThumbnailLoad()
                                    }
                                    StatusRequest.EMPTY -> {

                                    }
                                    StatusRequest.NOT_EMPTY ->{
                                        GridOfMediaThumbnail(
                                            multiMedia = multiMedia,
                                            thumbnail = getThumbnail,
                                            media = media,
                                            onSelectionMode = onSelectedMode,
                                            itemsSelected = { data -> index.value = data},
                                            state = state,
                                            userScrollEnabled = true,
                                            selectedIds = index
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    if (isSelectedMode) {
                        BottomAppBar (
                            actions = {
                                Box(modifier = Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                                    FilledTonalButton(onClick = {
                                        setStatePicker(StatePicker.ADD)
                                    }) {
                                        Text(modifier = Modifier.padding(start = 5.dp),text = stringResource(id = R.string.add) + if (multiMedia) "(${index.value.size})" else "", textAlign = TextAlign.Justify)
                                    }
                                }
                            },
                            contentPadding = PaddingValues(10.dp)
                        )
                    }
                }
            )
        }
    }
    if (openAlertDialog){
        AlertDialog(
            icon = {
                Icon(Icons.Default.Info, contentDescription = null)
            },
            title = {
                Text(text = stringResource(id = R.string.title_dialog_clean),textAlign = TextAlign.Justify)
            },
            text = {
                Text(text = stringResource(id = R.string.text_dialog_clean),textAlign = TextAlign.Justify)
            },
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        onOpenAlertDialog(false)
                        scope.launch {
                            index.value = emptySet()
                            setMedia(emptyList())
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.confirm),textAlign = TextAlign.Justify)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onOpenAlertDialog(false)
                    }
                ) {
                    Text(text = stringResource(id = R.string.dismiss),textAlign = TextAlign.Justify)
                }
            }
        )
    }
    LaunchedEffect(key1 = statePicker, block = {
        when (statePicker) {
            StatePicker.OPEN -> indexAux.value = index.value
            else -> {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    when (statePicker) {
                        StatePicker.DRAG, StatePicker.CLOSE -> if (mediaSelected.isEmpty()) {
                            index.value = emptySet()
                        } else {
                            if (index.value != indexAux.value){
                                index.value = indexAux.value
                            }
                        }
                        StatePicker.ADD -> {
                            setMedia(index.value.map { MediaUserV0(item = it, media = media[it]) })
                        }
                        else -> {}
                    }
                }
            }
        }
    })
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBarMediaViewer(
    multiMedia: Boolean,
    title: String,
    navIcon: () -> Unit,
    action: () -> Unit,
    isNotEmpty: Boolean
){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CenterAlignedTopAppBar(
            navigationIcon = {
                IconButton(onClick = navIcon) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                }
            },
            title = {
                Text(text = title)
            },
            actions = {
                if (multiMedia){
                    if (isNotEmpty) {
                        IconButton(onClick = action) {
                            Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                        }
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediaCarousel(
    media:List<MediaUserV0>,
    thumbnail: suspend (Uri, Long, String) -> Bitmap?,
    removeItem: (Int) -> Unit
){
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f){media.size}
    val scope = rememberCoroutineScope()
    HorizontalPager(
        contentPadding = PaddingValues(horizontal = 100.dp),
        state = pagerState,
    ) { page ->
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    val pageOffset = (
                            (pagerState.currentPage - page) + pagerState
                                .currentPageOffsetFraction
                            ).absoluteValue
                    lerp(
                        start = 0.85f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    ).also { scale ->
                        scaleX = scale
                        scaleY = scale
                    }
                    alpha = lerp(
                        start = 0.5f,
                        stop = 1f,
                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                    )
                },
            contentAlignment = Alignment.Center
        ){
            GetImage (
                modifier = Modifier.size(200.dp),
                page = page,
                thumbnail = {
                    thumbnail(
                        media[page].media.uriMedia,
                        media[page].media.idMedia,
                        media[page].media.mimeType
                    )
                }
            )
            Box (modifier = Modifier.size(200.dp), contentAlignment = Alignment.TopEnd){
                FilledTonalIconButton(
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer {
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue
                            lerp(
                                start = 0.85f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            ).also { scale ->
                                scaleX = scale
                                scaleY = scale
                            }
                        }
                    ,
                    onClick = {
                        scope.launch {
                            removeItem(media[page].item)
                        }
                    }
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null
                    )
                }
            }
        }
    }
}
@Composable
internal fun GridOfMediaThumbnailLoad(){
    LazyVerticalGrid(
        columns = GridCells.Fixed( 3 ),
        content = {
            items(18){
                Surface(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(3.dp),
                    tonalElevation = 3.dp
                ) {
                    Box(
                        modifier = Modifier
                            .then(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    Modifier.blur(25.dp)
                                } else {
                                    Modifier
                                }
                            )
                    )
                }
            }
        }
    )
}
@Composable
internal fun GridOfMediaThumbnail(
    multiMedia: Boolean,
    thumbnail: suspend (Uri, Long, String) -> Bitmap?,
    media:List<Media>,
    onSelectionMode: (Boolean) -> Unit,
    itemsSelected: (Set<Int>) -> Unit,
    state: LazyGridState,
    userScrollEnabled: Boolean,
    selectedIds: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
){
    val (prevItem,onPrevItem) = remember { mutableStateOf<Int?>(null) }
    val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty() } }
    val autoScrollSpeed = remember { mutableFloatStateOf(0f) }
    val (isDrag,onDrag) = rememberSaveable { mutableStateOf(false) }
    onSelectionMode(selectedIds.value.isNotEmpty())
    LaunchedEffect(key1 = inSelectionMode , block = {
        onPrevItem(
            if (selectedIds.value.isNotEmpty()) {
                selectedIds.value.first()
            } else null
        )
    })
    LaunchedEffect(autoScrollSpeed.floatValue) {
        if (autoScrollSpeed.floatValue != 0f) {
            while (isActive) {
                state.scrollBy(autoScrollSpeed.floatValue)
                delay(10)
            }
        }
    }
    itemsSelected(selectedIds.value)
    LazyVerticalGrid(
        columns = GridCells.Fixed( 3 ),
        modifier = Modifier.photoGridDragHandler(
            multiMedia = multiMedia,
            lazyGridState = state,
            haptics = LocalHapticFeedback.current,
            selectedIds = selectedIds,
            autoScrollSpeed = autoScrollSpeed,
            autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
            onDragStartListen = onDrag
        ),
        state = state,
        contentPadding = PaddingValues(horizontal = 3.dp),
        userScrollEnabled = userScrollEnabled,
        content = {
            items(media.size, key = { it }){item ->
                val selected by remember { derivedStateOf { selectedIds.value.contains(item) } }
                MediaItem(
                    itemPosition = if (selected) selectedIds.value.indexOf(item) + 1 else null,
                    multiMedia = multiMedia,
                    inSelectionMode = inSelectionMode,
                    selected = selected,
                    mime = media[item].mimeType.split('/')[0],
                    modifier = Modifier
                        .then(
                            if (!multiMedia){
                                //if (selectedIds.value.isEmpty())
                                Modifier.toggleable(
                                    value = selected,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onValueChange = {
                                        if (it) {
                                            selectedIds.value += item
                                            if (prevItem != null) {
                                                selectedIds.value = selectedIds.value.minus(prevItem)
                                            }
                                            onPrevItem(item)
                                        } else {
                                            selectedIds.value -= item
                                            if (prevItem != null) onPrevItem(null)
                                        }
                                    }
                                )
                            }
                            else {
                                if (inSelectionMode) {
                                    if (isDrag) {
                                        Modifier
                                    }
                                    else {
                                        Modifier.toggleable(
                                            value = selected,
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onValueChange = {
                                                if (it) {
                                                    selectedIds.value += item
                                                } else {
                                                    selectedIds.value -= item
                                                }
                                            }
                                        )
                                    }
                                }
                                else {
                                    Modifier.toggleable(
                                        value = selected,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null, // do not show a ripple
                                        onValueChange = {
                                            if (it) {
                                                selectedIds.value += item
                                            } else {
                                                selectedIds.value -= item
                                            }
                                        }
                                    )
                                }
                            }
                        ),
                    imageItem = {
                        GetImage (
                            page = item,
                            thumbnail = {
                                thumbnail(
                                    media[item].uriMedia,
                                    media[item].idMedia,
                                    media[item].mimeType
                                )
                            }
                        )
                    }
                )
            }
        }
    )
}
@Composable
internal fun MediaItem(
    itemPosition: Int?,
    multiMedia: Boolean,
    inSelectionMode: Boolean,
    selected: Boolean,
    mime: String,
    modifier: Modifier = Modifier,
    imageItem: @Composable () -> Unit
) {
    val bdColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
    val bgColor = MaterialTheme.colorScheme.primary
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp),
        tonalElevation = 3.dp
    ) {
        Box (contentAlignment = Alignment.Center) {
            imageItem()
            Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.TopEnd) {
                if (!multiMedia){
                    if (selected) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(0.2f)))
                        MyCenterTextInCanvas("$itemPosition",bdColor,bgColor)
                    }
                } else {
                    if (inSelectionMode) {
                        if (selected) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(0.2f))
                            )
                            MyCenterTextInCanvas("$itemPosition",bdColor,bgColor)
                        } else {
                            Icon(
                                Icons.Filled.RadioButtonUnchecked,
                                tint = Color.White.copy(alpha = 0.7f),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            when (mime){
                MIME_IMAGE -> {}
                MIME_VIDEO -> {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Rounded.PlayCircle,
                        contentDescription = null
                    )
                }
            }
            /*Icon(
                modifier = Modifier,
                imageVector = when (mime){
                    ConstantsMediaPicker.MIME_IMAGE -> {
                        Icons.Rounded.Image
                    }
                    ConstantsMediaPicker.MIME_VIDEO -> {
                        Icons.Rounded.PlayCircle
                    }
                    else -> {
                        Icons.Rounded.BrokenImage
                    }
                },
                contentDescription = null
            )*/
        }
    }
}
@SuppressLint("ModifierFactoryUnreferencedReceiver")
internal fun Modifier.photoGridDragHandler(
    multiMedia: Boolean,
    lazyGridState: LazyGridState,
    haptics: HapticFeedback,
    selectedIds: MutableState<Set<Int>>,
    autoScrollSpeed: MutableState<Float>,
    autoScrollThreshold: Float,
    onDragStartListen: (Boolean) -> Unit,
) = pointerInput(Unit) {
    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
        layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Int

    var initialKey: Int? = null
    var currentKey: Int? = null
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            onDragStartListen(true)
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                if (multiMedia){
                    if (!selectedIds.value.contains(key)) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        initialKey = key
                        currentKey = key
                        selectedIds.value += key
                    }
                }
            }
        },
        onDragCancel = { initialKey = null; autoScrollSpeed.value = 0f ; onDragStartListen(false) },
        onDragEnd = { initialKey = null; autoScrollSpeed.value = 0f; onDragStartListen(false) },
        onDrag = { change, _ ->
            if (initialKey != null) {
                val distFromBottom =
                    lazyGridState.layoutInfo.viewportSize.height - change.position.y
                val distFromTop = change.position.y
                autoScrollSpeed.value = when {
                    distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                    distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                    else -> 0f
                }

                lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                    if (currentKey != key) {
                        selectedIds.value = selectedIds.value
                            .minus(initialKey!!..currentKey!!)
                            .minus(currentKey!!..initialKey!!)
                            .plus((initialKey!!..key))
                            .plus((key..initialKey!!))
                        currentKey = key
                    }
                }
            }
        }
    )
}
@Composable
internal fun MyCenterTextInCanvas(item:String, bdColor: Color, bgColor: Color) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult: TextLayoutResult = textMeasurer.measure(text = AnnotatedString(item))
    val textSize = textLayoutResult.size
    Canvas(
        modifier = Modifier
            .border(2.dp, bdColor, CircleShape)
            .requiredSize(24.dp),
    ) {

        val canvasWidth = size.width
        val canvasHeight = size.height
        drawCircle(color = bgColor)
        drawText(
            textMeasurer = textMeasurer,
            text = item,
            style = TextStyle(color = bdColor),
            topLeft = Offset(
                (canvasWidth - textSize.width) / 2f,
                (canvasHeight - textSize.height) / 2f
            ),
        )
    }
}
@Composable
internal fun GetImage(
    modifier: Modifier = Modifier,
    page: Int,
    thumbnail: suspend () -> Bitmap?,
    contentTop: @Composable (() -> Unit)? = null
){
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(key1 = bitmap, block = {
        if (bitmap.value == null) {
            bitmap.value = thumbnail()
        }
    })
    Crossfade(
        targetState = bitmap.value,
        label = "transitionBitmap"
    ) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            when(it){
                is Bitmap -> {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                    when (contentTop){
                        is () -> Unit-> {
                            contentTop()
                        }
                    }
                }
                else ->{
                    Icon(
                        painter = painterResource(id = R.drawable.image_load),
                        contentDescription = null,
                        modifier = Modifier.scale(2f)
                    )
                }
            }
        }
    }
}