package api.luisangeldd.mediapicker

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.util.lerp
import api.luisangeldd.mediapicker.data.model.Media
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import api.luisangeldd.mediapicker.utils.IMAGE
import api.luisangeldd.mediapicker.utils.StateRequest
import api.luisangeldd.mediapicker.utils.StatusRequest
import api.luisangeldd.mediapicker.utils.VIDEO
import api.luisangeldd.mediapicker.utils.permissionsToRequest
import api.luisangeldd.mediapicker.utils.shimmerEffect
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import kotlin.math.absoluteValue

/**
 * <a href="https://m3.material.io/components/bottom-sheets/overview" class="external" target="_blank">Media Picker</a>.
 *
 * The media picker is used as an alternative, to the default file manager of the mobile device,
 * it provides the content of the device such as Images and Videos, it recovers the bitmaps of the
 * files and displays them in a LazyGrid allowing them to be selected and displayed the selected
 * content in a Carousel. Like dialog boxes, and modal bottom sheets, the media picker, appears
 * in front of the application content, disables all other functions of the application when it
 * appears and remains on the screen until confirmed, dismissed or a required action is performed.
 *
 * A simple example of a modal bottom sheet looks like this:
 *
 * @param getMedia returns a list of Media type objects which allows recovering the Uri and File of the selected files.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPicker(
    getMedia: (List<MediaUser>) -> Unit
){
    val mediaViewModel = getViewModel<MediaViewModel>()
    val isGranted = mediaViewModel.isGranted
    val media by mediaViewModel.media.collectAsState()
    val stateRequestMedia by mediaViewModel.stateRequestMedia.collectAsState()
    val statusRequestMedia by mediaViewModel.statusRequestMedia.collectAsState()
    val mediaSelected by mediaViewModel.mediaSelected.collectAsState()
    val mediaSelectedUser by mediaViewModel.mediaSelectedUser.collectAsState()
    val scope = rememberCoroutineScope()
    val state = rememberLazyGridState()
    val (isSelectedMode, onSelectedMode) = rememberSaveable { mutableStateOf(false) }
    val index: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            if (perms[permissionsToRequest[0]] == true && perms[permissionsToRequest[1]] == true) {
                scope.launch {
                    openBottomSheet = true
                }
                mediaViewModel.onPermissionResult(
                    isGranted = true
                )
            }
        }
    )
    LaunchedEffect(key1 = mediaSelectedUser, block = {
        getMedia(mediaSelectedUser)
    })
    Column (modifier = Modifier.fillMaxWidth() ){
        when (mediaSelected.isNotEmpty()){
            true ->{
                MediaCarousel(mediaSelected) {
                    scope.launch {
                        index.value -= it
                        val data = mutableListOf<MediaUserV0>()
                        index.value.forEach {
                            data.add(MediaUserV0(item =it,media = media[it]))
                        }.let {
                            mediaViewModel.setMedia(data)
                        }
                    }
                }
            }
            false ->{

            }
        }
        LaunchButton {
            multiplePermissionResultLauncher.launch(permissionsToRequest)
        }
    }
    if (openBottomSheet) {
        val windowInsets = if (edgeToEdgeEnabled)
            WindowInsets(0) else BottomSheetDefaults.windowInsets

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            windowInsets = windowInsets
        ) {
            Scaffold(
                topBar = {
                    TopBarMediaViewer(
                        title = "Media",
                        navIcon = {
                            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    openBottomSheet = false
                                }
                                if (mediaSelected.isEmpty()){
                                    index.value = emptySet()
                                }
                                else {
                                    if (mediaSelected.size != index.value.size){
                                        //index.value = indexAtSelect
                                    }
                                }
                            }
                        },
                        action = {
                            scope.launch {
                                index.value = emptySet()
                                mediaViewModel.setMedia(emptyList())
                            }
                        },
                        isNotEmpty = index.value.isNotEmpty()
                    )
                },
                content = { paddingInter ->
                    Box(Modifier.padding(paddingInter)){
                        if (isGranted.value){
                            when(stateRequestMedia){
                                StateRequest.IDLE -> {
                                    if(media.isEmpty()) mediaViewModel.getMedia()
                                }
                                StateRequest.START -> {
                                    GridOfMediaThumbnailLoad()
                                }
                                StateRequest.END -> {
                                    when(statusRequestMedia){
                                        StatusRequest.IDLE -> {}
                                        StatusRequest.EMPTY -> {}
                                        StatusRequest.NOT_EMPTY ->{
                                            GridOfMediaThumbnail(
                                                thumbnail = { uri, id, mime ->
                                                    mediaViewModel.getThumbnail(uri,id,mime)
                                                },
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
                    }
                },
                bottomBar = {
                    if (isSelectedMode) {
                        BottomAppBar (
                            actions = {
                                Box(modifier = Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                                    FilledTonalButton(onClick = {
                                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                            if (!bottomSheetState.isVisible) {
                                                openBottomSheet = false
                                            }
                                            val data = mutableListOf<MediaUserV0>()
                                            index.value.forEach {
                                                data.add(MediaUserV0(item =it,media = media[it]))
                                            }.let {
                                                mediaViewModel.setMedia(data)
                                            }
                                        }
                                    }) {
                                        Text(modifier = Modifier.padding(start = 5.dp),text = "Añadir (${index.value.size})")
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
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarMediaViewer(
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
                if (isNotEmpty) {
                    IconButton(onClick = action) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaCarousel(media:List<MediaUserV0>, removeItem: (Int) -> Unit){
    val pagerState = rememberPagerState(initialPage = 0, initialPageOffsetFraction = 0f){media.size}
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
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(media[page].media.uriMedia)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.image_load),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(200.dp)
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
                    onClick = { removeItem(media[page].item) }
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
fun LaunchButton(launch: () -> Unit){
    Box (modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
        FilledTonalIconButton(
            modifier = Modifier.size(100.dp),
            onClick = launch
        ) {
            Icon(modifier = Modifier.size(100.dp),imageVector = Icons.Rounded.Add, contentDescription = null)
        }
    }
}
@Composable
private fun GridOfMediaThumbnailLoad(){
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
                    Box(modifier = Modifier.shimmerEffect())
                }
            }
        }
    )
}
@Composable
private fun GridOfMediaThumbnail(
    thumbnail: (Uri, Long, String) -> Bitmap,
    media:List<Media>,
    onSelectionMode: (Boolean) -> Unit,
    itemsSelected: (Set<Int>) -> Unit,
    state: LazyGridState,
    userScrollEnabled: Boolean,
    selectedIds: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
){
    val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty() } }
    val autoScrollSpeed = remember { mutableFloatStateOf(0f) }
    val (isDrag,onDrag) = rememberSaveable { mutableStateOf(false) }
    onSelectionMode(selectedIds.value.isNotEmpty())
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
                    thumbnail = { thumbnail(media[item].uriMedia,media[item].idMedia,media[item].mimeType) },
                    inSelectionMode,
                    selected,
                    media[item].mimeType.split('/')[0],
                    Modifier
                        .then(
                            if (inSelectionMode) {
                                if (isDrag) {
                                    Modifier
                                } else {
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
                            } else {
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
                        )
                )
            }
        }
    )
}
@Composable
private fun MediaItem(
    thumbnail: () -> Bitmap,
    inSelectionMode: Boolean,
    selected: Boolean,
    mime: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp),
        tonalElevation = 3.dp
    ) {
        Box (contentAlignment = Alignment.Center) {
            Image(
                bitmap = thumbnail().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .matchParentSize(),
                contentScale = ContentScale.Crop
            )
            if (inSelectionMode) {
                Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.TopEnd) {
                    if (selected) {
                        val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(0.2f)))
                        Icon(
                            Icons.Filled.CheckCircle,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(4.dp)
                                .border(2.dp, bgColor, CircleShape)
                                .clip(CircleShape)
                                .background(bgColor)
                        )
                    } else {
                        Icon(
                            Icons.Filled.RadioButtonUnchecked,
                            tint = Color.White.copy(alpha = 0.7f),
                            contentDescription = null,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            Icon(
                modifier = Modifier.size(48.dp),
                imageVector = when (mime){
                    IMAGE -> {
                        Icons.Rounded.Image
                    }
                    VIDEO -> {
                        Icons.Rounded.PlayCircle
                    }
                    else -> {
                        Icons.Rounded.BrokenImage
                    }
                },
                contentDescription = null
            )
        }
    }
}
private fun Modifier.photoGridDragHandler(
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
                if (!selectedIds.value.contains(key)) {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    initialKey = key
                    currentKey = key
                    selectedIds.value += key
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