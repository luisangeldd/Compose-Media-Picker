package api.luisangeldd.mediapicker.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
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
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import api.luisangeldd.mediapicker.R
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.FOLDERS
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.FOLDER_CONTENT
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.FOLDER_NAME
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.arg
import api.luisangeldd.mediapicker.core.ConstantsMediaPicker.argSend
import api.luisangeldd.mediapicker.data.model.AlbumData
import api.luisangeldd.mediapicker.data.model.MediaData
import api.luisangeldd.mediapicker.data.model.MediaUserV0
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LayoutOfMediaPicker(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    topAppBarFromMediaPicker: @Composable () -> Unit,
    contentFromMediaPicker: @Composable () -> Unit,
){
    ModalBottomSheet(
        modifier = Modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            topAppBarFromMediaPicker()
            contentFromMediaPicker()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBarMediaPicker(
    selectedPager: Boolean,
    goToPhoto: (Boolean) -> Unit,
    goToAlbum: (Boolean) -> Unit,
    closeMediaPicker: () -> Unit,
){
    CenterAlignedTopAppBar(
        title = {
            Row {
                ChipOfMenu(
                    text = "Photos",
                    selected = selectedPager,
                    onSelected = goToPhoto
                )
                Spacer(modifier = Modifier.padding(10.dp))
                ChipOfMenu(
                    text = "Albums",
                    selected = !selectedPager,
                    onSelected = goToAlbum
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = closeMediaPicker,
            ) {
                Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBarMediaByFolder(
    folder: String,
    onBackPressed: () -> Unit
){
    TopAppBar(
        title = {
            Text(text = folder)
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed){
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}
@Composable
internal fun BottomAppBarMediaPicker(
    addItems: () -> Unit,
    removeItem: @Composable () -> Unit,
    goToTop:  @Composable () -> Unit,
    items: String
){
    BottomAppBar (
        actions = {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                removeItem()
                FilledTonalButton(onClick = addItems) {
                    Text(stringResource(id = R.string.add)+" ($items)")
                }
            }
        },
        floatingActionButton = goToTop,
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}
@Composable
internal fun ViewOfMedia(
    state: PagerState,
    contentPage: @Composable (Int) -> Unit,
){
    HorizontalPager(
        state = state,
        userScrollEnabled = false
    ) {
        contentPage(it)
    }
}
@Composable
internal fun ContentOfMedia(
    topAppBarFromMediaPicker: @Composable () -> Unit = {},
    contentFromMediaPicker: @Composable (PaddingValues) -> Unit,
    bottomAppBarFromMediaPicker: @Composable () -> Unit = {},
    floatingActionButtonFromMediaPicker: @Composable () -> Unit = {}
){
    Scaffold(
        topBar = topAppBarFromMediaPicker,
        content = contentFromMediaPicker,
        bottomBar = bottomAppBarFromMediaPicker,
        floatingActionButton = floatingActionButtonFromMediaPicker,
        floatingActionButtonPosition = FabPosition.EndOverlay,
        contentWindowInsets =  WindowInsets(0, 0, 0, 0)
    )
}
@Composable
internal fun MessageClearSelection(
    scope: CoroutineScope = rememberCoroutineScope(),
    onOpenAlertDialog: (Boolean) -> Unit,
    index: MutableState<Set<Int>>,
    setMediaCollect: (List<MediaUserV0>) -> Unit,
    hideDialog: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) }
){
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = null)
        },
        title = {
            Text(text = stringResource(id = R.string.title_dialog_clean),textAlign = TextAlign.Justify)
        },
        text = {
            Column {
                Text(
                    text = stringResource(id = R.string.text_dialog_clean),
                    textAlign = TextAlign.Justify
                )
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "No volver a mostrar este cuadro")
                    Checkbox(
                        checked = hideDialog.value,
                        onCheckedChange = {
                            hideDialog.value = it
                        }
                    )
                }
            }
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onOpenAlertDialog(false)
                    scope.launch {
                        index.value = emptySet()
                        setMediaCollect(emptyList())
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
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MediaCarousel(
    mediaSelected: List<MediaUserV0>,
    removeItem: (Int) -> Unit
){
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = {mediaSelected.size}
    )
    HorizontalPager(
        modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout),
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
                }
                .size(200.dp),
            contentAlignment = Alignment.Center
        ){
            AsyncImage(
                modifier = Modifier.size(200.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(mediaSelected[page].media.uriMedia)
                    //.videoFrameMillis(1000)
                    .build(),//,
                contentDescription = mediaSelected[page].media.uriMedia.hashCode().toString()
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
                            removeItem(mediaSelected[page].item)
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
internal fun GridOfMediaLoad(page: Int){
    LazyVerticalGrid(
        columns = GridCells.Fixed( if (page == 0) 3 else 2 ),
        content = {
            items(if (page == 0) 18 else 8){
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(3.dp)
                        .shimmer()
                        .background(color = MaterialTheme.colorScheme.surface)
                        .blur(25.dp)
                )
            }
        }
    )
}
@Composable
internal fun GridOfMediaLoaded(
    paddingValues: PaddingValues,
    multiMedia: Boolean,
    media:List<MediaData>,
    isSelectedMode: MutableState<Boolean>,
    itemsSelected: (Set<Int>) -> Unit,
    stateLazyGridPhoto: LazyGridState = rememberLazyGridState(),
    isScrolling: MutableState<Boolean>,
    userScrollEnabled: Boolean,
    selectedIds: MutableState<Set<Int>> = rememberSaveable { mutableStateOf(emptySet()) }
){
    val modifier = when (isSelectedMode.value) {
        true -> Modifier.padding(paddingValues)
        false -> Modifier.padding(top = paddingValues.calculateTopPadding())
    }
    val (prevItem,onPrevItem) = remember { mutableStateOf<Int?>(null) }
    val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty() } }
    val autoScrollSpeed = remember { mutableFloatStateOf(0f) }
    val (isDrag,onDrag) = rememberSaveable { mutableStateOf(false) }
    isSelectedMode.value = selectedIds.value.isNotEmpty()

    LaunchedEffect(key1 = isSelectedMode.value , block = {
        onPrevItem(
            if (selectedIds.value.isNotEmpty()) {
                selectedIds.value.first()
            } else null
        )
    })
    LaunchedEffect(autoScrollSpeed.floatValue) {
        if (autoScrollSpeed.floatValue != 0f) {
            while (isActive) {
                stateLazyGridPhoto.scrollBy(autoScrollSpeed.floatValue)
                delay(10)
            }
        }
    }
    itemsSelected(selectedIds.value)
    LazyVerticalGrid(
        columns = GridCells.Fixed( 3 ),
        modifier = modifier
            .padding()
            .photoGridDragHandler(
                multiMedia = multiMedia,
                lazyGridState = stateLazyGridPhoto,
                haptics = LocalHapticFeedback.current,
                selectedIds = selectedIds,
                autoScrollSpeed = autoScrollSpeed,
                autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() },
                onDragStartListen = onDrag
            ),
        state = stateLazyGridPhoto,
        contentPadding = PaddingValues(horizontal = 3.dp),
        userScrollEnabled = userScrollEnabled,
        content = {
            items(
                items = media,
                key = { it.idMedia.toInt() }
            ){ media ->
                val selected by remember { derivedStateOf { selectedIds.value.contains(media.idMedia.toInt()) } }
                val mime = media.mimeType.split('/')[0]
                MediaItem(
                    itemPosition = if (selected) selectedIds.value.indexOf(media.idMedia.toInt()) + 1 else null,
                    multiMedia = multiMedia,
                    inSelectionMode = inSelectionMode,
                    selected = selected,
                    modifier = Modifier
                        .then(
                            if (!multiMedia){
                                Modifier.toggleable(
                                    value = selected,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onValueChange = {
                                        if (it) {
                                            selectedIds.value += media.idMedia.toInt()
                                            if (prevItem != null) {
                                                selectedIds.value = selectedIds.value.minus(prevItem)
                                            }
                                            onPrevItem(media.idMedia.toInt())
                                        } else {
                                            selectedIds.value -= media.idMedia.toInt()
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
                                                    selectedIds.value += media.idMedia.toInt()
                                                } else {
                                                    selectedIds.value -= media.idMedia.toInt()
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
                                                selectedIds.value += media.idMedia.toInt()
                                            } else {
                                                selectedIds.value -= media.idMedia.toInt()
                                            }
                                        }
                                    )
                                }
                            }
                        ),
                    imageItem = {
                        GetImage (
                            modifier = Modifier.scale(2f),
                            url = media.uriMedia.toString(),
                            contentTop = {
                                when (mime){
                                    ConstantsMediaPicker.MIME_IMAGE -> {null}
                                    ConstantsMediaPicker.MIME_VIDEO -> {
                                        Icon(
                                            imageVector = Icons.Rounded.PlayCircle,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        )
                    }
                )
            }
        }
    )
    isScrolling.value = (stateLazyGridPhoto.firstVisibleItemIndex >= 6)
}
@Composable
internal fun GridOfFoldersLoaded(
    paddingValues: PaddingValues,
    dataFolder: List<AlbumData>,
    stateLazyGridAlbum: LazyGridState = rememberLazyGridState(),
    getItemsByFolder: (String, String) -> Unit,
    isScrolling: MutableState<Boolean>
){
    val modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
    LazyVerticalGrid(
        columns = GridCells.Fixed( 2 ),
        modifier = modifier.fillMaxSize(),
        state = stateLazyGridAlbum,
        contentPadding = PaddingValues(horizontal = 3.dp),
        content = {
            items(dataFolder.size, key = {it}){item ->
                MediaFolder(
                    modifier = Modifier.clickable {
                        getItemsByFolder(
                            dataFolder[item].pathFromFolder.absolutePath,
                            FOLDER_CONTENT argSend dataFolder[item].pathFromFolder.nameWithoutExtension
                        )
                    },
                    url = dataFolder[item].uri.toString(),
                    titleFolder = dataFolder[item].pathFromFolder.nameWithoutExtension,
                    items = dataFolder[item].itemsFolder
                )
                isScrolling.value = (stateLazyGridAlbum.firstVisibleItemIndex >= 4)
            }
        }
    )
}
@Composable
internal fun MediaByFolder(
    navController: NavHostController = rememberNavController(),
    albums: @Composable (PaddingValues,(String) -> Unit) -> Unit ,
    mediaByAlbum: @Composable (PaddingValues) -> Unit = {},
    bottomAppBarFromMediaPickerToMediaByAlbum: @Composable () -> Unit = {},
    floatingActionButtonFromMediaPickerToAlbums: @Composable () -> Unit = {},
    floatingActionButtonFromMediaPickerToMediaByAlbum: @Composable () -> Unit = {}
){
    NavHost(
        navController = navController,
        startDestination = FOLDERS
    ){
        composable(
            route = FOLDERS,
            content = {
                ContentOfMedia(
                    contentFromMediaPicker = { pdd ->
                        albums( pdd ) { folderName ->
                            navController.navigate(folderName)
                        }
                    },
                    floatingActionButtonFromMediaPicker = floatingActionButtonFromMediaPickerToAlbums
                )
            }
        )
        composable(
            route = FOLDER_CONTENT arg FOLDER_NAME,
            arguments = listOf(navArgument(FOLDER_NAME) {
                type = NavType.StringType
            }),
            content = {
                val folder = it.arguments?.getString(FOLDER_NAME) ?: ""
                ContentOfMedia(
                    topAppBarFromMediaPicker = {
                        TopAppBarMediaByFolder(
                            folder = folder,
                            onBackPressed = navController::popBackStack
                        )
                    },
                    contentFromMediaPicker = mediaByAlbum,
                    bottomAppBarFromMediaPicker = bottomAppBarFromMediaPickerToMediaByAlbum,
                    floatingActionButtonFromMediaPicker = floatingActionButtonFromMediaPickerToMediaByAlbum
                )
            }
        )
    }
}
@Composable
internal fun MediaFolder(
    modifier: Modifier = Modifier,
    url: String,
    titleFolder: String,
    items: Int
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Surface(
            modifier = modifier
                .aspectRatio(1f)
                .padding(3.dp),
            tonalElevation = 3.dp,
            color = Color.Transparent
        ) {
            GetImage(
                modifier = Modifier.scale(3f),
                url = url,
            )
        }
        Text(modifier = Modifier,text = titleFolder, maxLines = 1)
        Text(modifier = Modifier,text = "$items")
    }
}
@Composable
internal fun MediaItem(
    itemPosition: Int?,
    multiMedia: Boolean,
    inSelectionMode: Boolean,
    selected: Boolean,
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
        }
    }
}
@Composable
internal fun ChipOfMenu(
    text : String,
    selected : Boolean,
    onSelected : (Boolean) -> Unit
){
    FilterChip(
        selected = selected,
        onClick = { onSelected(selected) },
        label = { Text(text) },
        leadingIcon = null
    )
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
    url: String,
    contentTop: @Composable (() -> Unit)? = null
){
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        AsyncImage(
            modifier = Modifier.size(200.dp),
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .build(),
            contentDescription = url.hashCode().toString()
        )
        if (contentTop != null){
            contentTop()
        }
    }
}