package api.luisangeldd.mediapicker.ui

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import api.luisangeldd.mediapicker.R
import api.luisangeldd.mediapicker.core.IMAGE
import api.luisangeldd.mediapicker.core.StatePicker
import api.luisangeldd.mediapicker.core.StateRequest
import api.luisangeldd.mediapicker.core.StatusRequest
import api.luisangeldd.mediapicker.core.VIDEO
import api.luisangeldd.mediapicker.core.permissionsToRequest
import api.luisangeldd.mediapicker.core.shimmerEffect
import api.luisangeldd.mediapicker.data.model.Media
import api.luisangeldd.mediapicker.data.model.MediaUser
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import kotlin.math.absoluteValue

@Composable
fun MediaPicker(
    injectionByHilt: Boolean = true,
    getMedia: (List<MediaUser>) -> Unit
){
    if (injectionByHilt) {
        MediaPickerHilt(mediaViewModel = hiltViewModel<Hilt>(),getMedia =getMedia)
    } else {
        MediaPickerKoin(mediaViewModel = getViewModel<Koin>(),getMedia =getMedia)
    }
}
@Composable
private fun MediaPickerHilt(
    mediaViewModel: Hilt,
    getMedia: (List<MediaUser>) -> Unit
){
    val statePicker by mediaViewModel.statePicker.collectAsState()
    val media by mediaViewModel.media.collectAsState()
    val stateRequestMedia by mediaViewModel.stateRequestMedia.collectAsState()
    val statusRequestMedia by mediaViewModel.statusRequestMedia.collectAsState()
    val mediaSelected by mediaViewModel.mediaSelected.collectAsState()
    val mediaSelectedUser by mediaViewModel.mediaSelectedUser.collectAsState()
    MediaPickerApp(
        statePicker = statePicker,
        media = media,
        stateRequestMedia = stateRequestMedia,
        statusRequestMedia = statusRequestMedia,
        mediaSelected = mediaSelected,
        mediaSelectedUser = mediaSelectedUser,
        getThumbnail = { uri,long,mime -> mediaViewModel.getThumbnail(uri,long,mime)},
        setMedia = { mediaViewModel.setMedia(it)},
        setStatePicker = { mediaViewModel.statePicker(it)},
        setMediaCollect = {getMedia(mediaSelectedUser)},
        getMedia = { mediaViewModel.getMedia() }
    )
}
@Composable
private fun MediaPickerKoin(
    mediaViewModel: Koin,
    getMedia: (List<MediaUser>) -> Unit
){
    val statePicker by mediaViewModel.statePicker.collectAsState()
    val media by mediaViewModel.media.collectAsState()
    val stateRequestMedia by mediaViewModel.stateRequestMedia.collectAsState()
    val statusRequestMedia by mediaViewModel.statusRequestMedia.collectAsState()
    val mediaSelected by mediaViewModel.mediaSelected.collectAsState()
    val mediaSelectedUser by mediaViewModel.mediaSelectedUser.collectAsState()
    MediaPickerApp(
        statePicker = statePicker,
        media = media,
        stateRequestMedia = stateRequestMedia,
        statusRequestMedia = statusRequestMedia,
        mediaSelected = mediaSelected,
        mediaSelectedUser = mediaSelectedUser,
        getThumbnail = { uri,long,mime -> mediaViewModel.getThumbnail(uri,long,mime)},
        setMedia = { mediaViewModel.setMedia(it)},
        setStatePicker = { mediaViewModel.statePicker(it)},
        setMediaCollect = {getMedia(mediaSelectedUser)},
        getMedia = { mediaViewModel.getMedia() }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaPickerApp(
    statePicker: StatePicker,
    media: List<Media>,
    stateRequestMedia: StateRequest,
    statusRequestMedia: StatusRequest,
    mediaSelected: List<MediaUserV0>,
    mediaSelectedUser: List<MediaUser>,
    getThumbnail: (Uri, Long, String) -> Bitmap,
    setMedia: (List<MediaUserV0>) -> Unit,
    setStatePicker:(StatePicker ) -> Unit,
    setMediaCollect: () -> Unit,
    getMedia: () -> Unit
){
    val scope = rememberCoroutineScope()
    val state = rememberLazyGridState()
    val (isSelectedMode, onSelectedMode) = rememberSaveable { mutableStateOf(false) }
    val index: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { perms ->
            scope.launch {
                if (perms[permissionsToRequest[0]] == true && perms[permissionsToRequest[1]] == true) {
                    setStatePicker(StatePicker.OPEN)
                }
            }
        }
    )
    val openAlertDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = mediaSelectedUser, block = {
        setMediaCollect()
    })

    Column (modifier = Modifier.fillMaxWidth() ){
        when (mediaSelected.isNotEmpty()){
            true ->{
                MediaCarousel(mediaSelected, thumbnail = { uri,id,mime -> getThumbnail(uri,id,mime) }) {
                    scope.launch {
                        index.value -= it
                        setMedia(index.value.map { MediaUserV0(item = it,media = media[it]) })
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
    if (statePicker == StatePicker.OPEN) {
        ModalBottomSheet(
            onDismissRequest = {setStatePicker(StatePicker.DRAG) },
            sheetState = bottomSheetState
        ) {
            Scaffold(
                topBar = {
                    TopBarMediaViewer(
                        title = "Media",
                        navIcon = {
                            setStatePicker(StatePicker.CLOSE)
                        },
                        action = {
                            openAlertDialog.value = true
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
                                    StatusRequest.IDLE -> {}
                                    StatusRequest.EMPTY -> {}
                                    StatusRequest.NOT_EMPTY ->{
                                        GridOfMediaThumbnail(
                                            thumbnail = { uri, id, mime ->
                                                getThumbnail(uri,id,mime)
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
                },
                bottomBar = {
                    if (isSelectedMode) {
                        BottomAppBar (
                            actions = {
                                Box(modifier = Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                                    FilledTonalButton(onClick = {
                                        setStatePicker(StatePicker.ADD)
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
    if (openAlertDialog.value){
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
                        openAlertDialog.value = false
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
                        openAlertDialog.value = false
                    }
                ) {
                    Text(text = stringResource(id = R.string.dismiss),textAlign = TextAlign.Justify)
                }
            }
        )
    }
    LaunchedEffect(key1 = statePicker, block = {
        when (statePicker) {
            StatePicker.OPEN -> {}
            else -> {
                scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    when (statePicker) {
                        StatePicker.DRAG, StatePicker.CLOSE -> {
                            if (mediaSelected.isEmpty()){
                                index.value = emptySet()
                            }
                        }
                        StatePicker.ADD -> {
                            setMedia(index.value.map { MediaUserV0(item = it,media = media[it]) })
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
private fun MediaCarousel(media:List<MediaUserV0>,thumbnail: (Uri,Long,String) -> Bitmap, removeItem: (Int) -> Unit){
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
                    .data(thumbnail(media[page].media.uriMedia,media[page].media.idMedia,media[page].media.mimeType))
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
private fun LaunchButton(launch: () -> Unit){
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
                    Box(
                        modifier = Modifier
                            .then(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Modifier.blur(25.dp)
                                } else {
                                    Modifier.shimmerEffect()
                                }
                            )
                    )
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