package de.osca.android.public_transport.presentation.public_transport

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import de.osca.android.essentials.presentation.component.design.BaseCardContainer
import de.osca.android.essentials.presentation.component.design.BaseTextField
import de.osca.android.essentials.presentation.component.design.DateTimePickerElement
import de.osca.android.essentials.presentation.component.design.MainButton
import de.osca.android.essentials.presentation.component.design.MainDatePickerDialog
import de.osca.android.essentials.presentation.component.design.MainTimePickerDialog
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.presentation.component.design.ModuleDesignArgs
import de.osca.android.essentials.presentation.component.design.RootContainer
import de.osca.android.essentials.presentation.component.design.SimpleSpacedList
import de.osca.android.essentials.presentation.component.screen_wrapper.ScreenWrapper
import de.osca.android.essentials.presentation.component.topbar.ResponsiveText
import de.osca.android.essentials.presentation.component.topbar.ScreenTopBar
import de.osca.android.essentials.utils.extensions.SetSystemStatusBar
import de.osca.android.essentials.utils.extensions.getLastDeviceLocation
import de.osca.android.essentials.utils.extensions.isToday
import de.osca.android.essentials.utils.extensions.isTomorrow
import de.osca.android.essentials.utils.extensions.safeLet
import de.osca.android.essentials.utils.extensions.setOnCurrentState
import de.osca.android.essentials.utils.extensions.toDateString
import de.osca.android.essentials.utils.extensions.toDateTimeString
import de.osca.android.essentials.utils.extensions.toTimeString
import de.osca.android.public_transport.R
import de.osca.android.public_transport.domain.entity.StopType
import de.osca.android.public_transport.domain.entity.TripTempData
import de.osca.android.public_transport.domain.entity.nearby.StopsNearbyResponse
import de.osca.android.public_transport.navigation.PublicTransportNavItems
import de.osca.android.public_transport.navigation.PublicTransportNavItems.RouteDetails.BUNDLE_KEY_TRIP
import de.osca.android.public_transport.presentation.component.CommuteButton
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PublicTransportScreen(
    fromId: String? = null,
    toId: String? = null,
    dateTimeArg: String? = null,
    arrDep: String? = null,
    navController: NavController,
    publicTransportViewModel: PublicTransportViewModel = hiltViewModel(),
    masterDesignArgs: MasterDesignArgs = publicTransportViewModel.defaultDesignArgs,
) {
    val context = LocalContext.current
    val design = publicTransportViewModel.designArgs

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val selectionStopType = remember { mutableStateOf(StopType.DESTINATION) }
    val datePickerState = rememberMaterialDialogState()
    val timePickerState = rememberMaterialDialogState()
    val dateTime =
        remember { if (dateTimeArg != null) mutableStateOf(LocalDateTime.parse(dateTimeArg)) else TripTempData.tripTempDateTime }
    val currentYear = LocalDateTime.now().year
    val stopSearchValue = remember { mutableStateOf("") }
    val stopSearchTextFieldFocus = FocusRequester()

    LaunchedEffect(Unit) {
        if (fromId == null && toId == null) {
            context.getLastDeviceLocation { result ->
                result?.let { latLng ->
                    publicTransportViewModel.fetchStopsNearBy(latLng)
                }
            }
        }
        publicTransportViewModel.checkedStateOrigin.value = arrDep != "arr"
        publicTransportViewModel.fetchStopsById(fromId, toId, dateTime.value)
    }

    MainDatePickerDialog(
        yearRange = IntRange(currentYear, currentYear + 1),
        datePickerState = datePickerState,
        onDatePicked = { pickedDate ->
            val newDateTime =
                dateTime.value
                    .withYear(pickedDate.year)
                    .withMonth(pickedDate.monthValue)
                    .withDayOfMonth(pickedDate.dayOfMonth)
            dateTime.value = newDateTime
            TripTempData.tripTempDateTime.value = newDateTime
        },
        masterDesignArgs = masterDesignArgs,
    )

    MainTimePickerDialog(
        timePickerState = timePickerState,
        onTimePicked = { pickedTime ->
            val newDateTime =
                dateTime.value
                    .withHour(pickedTime.hour)
                    .withMinute(pickedTime.minute)
                    .withSecond(pickedTime.second)
            dateTime.value = newDateTime
            TripTempData.tripTempDateTime.value = newDateTime
        },
        masterDesignArgs = masterDesignArgs,
    )

    val bottomSheetState =
        rememberBottomSheetScaffoldState(
            bottomSheetState =
                BottomSheetState(BottomSheetValue.Collapsed, LocalDensity.current) {
                    if (it == BottomSheetValue.Collapsed) {
                        stopSearchValue.value = ""
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                    true
                },
        )

    SetSystemStatusBar(
        !(design.mIsStatusBarWhite ?: masterDesignArgs.mIsStatusBarWhite),
        Color.Transparent,
    )

    ScreenWrapper(
        topBar = {
            ScreenTopBar(
                title = stringResource(id = design.vModuleTitle),
                navController = navController,
                overrideTextColor = design.mTopBarTextColor,
                overrideBackgroundColor = design.mTopBarBackColor,
                masterDesignArgs = masterDesignArgs,
            )
        },
        navController = navController,
        screenWrapperState = publicTransportViewModel.wrapperState,
        retryAction = {
            context.getLastDeviceLocation { result ->
                result?.let { latLng ->
                    publicTransportViewModel.fetchStopsNearBy(latLng)
                }
            }
        },
        masterDesignArgs = masterDesignArgs,
        moduleDesignArgs = design,
    ) {
        BottomSheetScaffold(
            modifier =
                Modifier.semantics {
                    testTag = "BottomSheetScaffold"
                },
            sheetBackgroundColor = design.mSheetBackColor ?: masterDesignArgs.mSheetBackColor,
            scaffoldState = bottomSheetState,
            sheetShape = masterDesignArgs.mShapeBottomSheet,
            sheetElevation = masterDesignArgs.mSheetElevation,
            sheetContent = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(300.dp, screenHeight - 180.dp)
                            .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BottomSheetContent(
                        publicTransportViewModel = publicTransportViewModel,
                        bottomSheetState = bottomSheetState,
                        stopType = selectionStopType,
                        stopSearchValue = stopSearchValue,
                        stopSearchTextFieldFocus = stopSearchTextFieldFocus,
                        masterDesignArgs = masterDesignArgs,
                        moduleDesignArgs = design,
                    )
                }
            },
            sheetPeekHeight = 0.dp,
        ) {
            PublicTransportScreenContent(
                publicTransportViewModel,
                navController,
                masterDesignArgs,
                design,
                bottomSheetState,
                selectionStopType,
                stopSearchTextFieldFocus,
                stopSearchValue,
                datePickerState,
                timePickerState,
                dateTime,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun BottomSheetContent(
    publicTransportViewModel: PublicTransportViewModel,
    bottomSheetState: BottomSheetScaffoldState,
    stopType: MutableState<StopType>,
    stopSearchValue: MutableState<String>,
    stopSearchTextFieldFocus: FocusRequester,
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val suggestions = remember { publicTransportViewModel.myStops }

    Column(
        modifier =
            Modifier
                .fillMaxHeight()
                .padding(top = 10.dp)
                .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_draggable),
            tint = moduleDesignArgs.mHintTextColor ?: masterDesignArgs.mHintTextColor,
            contentDescription = "",
            modifier =
                Modifier
                    .padding(top = 10.dp),
        )

        LazyColumn(
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            item {
                BaseTextField(
                    textFieldTitle =
                        stringResource(
                            id =
                                when (stopType.value) {
                                    StopType.ORIGIN -> R.string.public_transport_search_start
                                    StopType.DESTINATION -> R.string.public_transport_search_stop
                                },
                        ),
                    textValue = stopSearchValue,
                    onTextChange = { newValue ->
                        publicTransportViewModel.fetchStops(newValue)
                    },
                    focusRequester = stopSearchTextFieldFocus,
                    masterDesignArgs = masterDesignArgs,
                    moduleDesignArgs = moduleDesignArgs,
                )
            }
            items(
                items = suggestions,
                itemContent = { item ->
                    Text(
                        text = item.name,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                                .clickable {
                                    println("###########")
                                    println(item.id)
                                    println("###########")
                                    when (stopType.value) {
                                        StopType.ORIGIN -> {
                                            publicTransportViewModel.originStop.value = item
                                        }

                                        StopType.DESTINATION -> {
                                            publicTransportViewModel.destinationStop.value = item
                                        }
                                    }

                                    coroutineScope.launch {
                                        bottomSheetState.bottomSheetState.collapse()
                                    }

                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                },
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PublicTransportScreenContent(
    publicTransportViewModel: PublicTransportViewModel,
    navController: NavController,
    masterDesignArgs: MasterDesignArgs,
    moduleDesignArgs: ModuleDesignArgs,
    bottomSheetState: BottomSheetScaffoldState,
    selectionStopType: MutableState<StopType>,
    stopSearchTextFieldFocus: FocusRequester,
    stopSearchValue: MutableState<String>,
    datePickerState: MaterialDialogState,
    timePickerState: MaterialDialogState,
    dateTime: MutableState<LocalDateTime>,
) {
    val coroutineScope = rememberCoroutineScope()

    fun showBottomSheet(stopType: StopType) {
        coroutineScope.launch {
            stopSearchValue.value = ""
            selectionStopType.value = stopType
            bottomSheetState.bottomSheetState.expand()
            stopSearchTextFieldFocus.requestFocus()
        }
    }

    val origin = remember { publicTransportViewModel.originStop }
    val destination = remember { publicTransportViewModel.destinationStop }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(moduleDesignArgs.mScreenBackColor ?: masterDesignArgs.mScreenBackColor),
    ) {
        RootContainer(
            masterDesignArgs = masterDesignArgs,
            moduleDesignArgs = moduleDesignArgs,
        ) {
            item {
                BaseCardContainer(
                    masterDesignArgs = masterDesignArgs,
                    moduleDesignArgs = moduleDesignArgs,
                ) {
                    SimpleSpacedList(
                        masterDesignArgs = masterDesignArgs,
                        overrideSpace = 16.dp,
                    ) {
                        Box {
                            Box(
                                Modifier
                                    .align(Alignment.CenterEnd)
                                    .zIndex(1f)
                                    .offset(x = -10.dp)
                                    .background(
                                        moduleDesignArgs.mDialogsBackColor
                                            ?: masterDesignArgs.mDialogsBackColor,
                                        RoundedCornerShape(
                                            moduleDesignArgs.mShapeCard
                                                ?: masterDesignArgs.mShapeCard,
                                        ),
                                    )
                                    .clickable {
                                        val temp = publicTransportViewModel.originStop.value
                                        publicTransportViewModel.originStop.value =
                                            publicTransportViewModel.destinationStop.value
                                        publicTransportViewModel.destinationStop.value = temp
                                    }
                                    .requiredSize(40.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = de.osca.android.public_transport.R.drawable.ic_switch_arrows),
                                    contentDescription = "",
                                    tint =
                                        moduleDesignArgs.mDialogsTextColor
                                            ?: masterDesignArgs.mDialogsTextColor,
                                    modifier =
                                        Modifier
                                            .requiredSize(25.dp),
                                )
                            }
                            Column(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .zIndex(-1f),
                            ) {
                                Row {
                                    OutlinedTextField(
                                        value = origin.value?.name ?: "",
                                        enabled = false,
                                        singleLine = true,
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(0.dp, 5.dp)
                                                .wrapContentHeight()
                                                .clickable {
                                                    showBottomSheet(StopType.ORIGIN)
                                                },
                                        onValueChange = {},
                                        textStyle = masterDesignArgs.normalTextStyle,
                                        colors = masterDesignArgs.getTextFieldColors(),
                                        shape =
                                            RoundedCornerShape(
                                                moduleDesignArgs.mShapeCard
                                                    ?: masterDesignArgs.mShapeCard,
                                            ),
                                        label = {
                                            Text(
                                                text = stringResource(id = R.string.public_transport_search_start),
                                                color = masterDesignArgs.mHintTextColor,
                                                style = masterDesignArgs.normalTextStyle,
                                            )
                                        },
                                    )
                                }

                                Row {
                                    OutlinedTextField(
                                        value = destination.value?.name ?: "",
                                        enabled = false,
                                        singleLine = true,
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(0.dp, 5.dp)
                                                .wrapContentHeight()
                                                .clickable {
                                                    showBottomSheet(StopType.DESTINATION)
                                                },
                                        onValueChange = {},
                                        textStyle = masterDesignArgs.normalTextStyle,
                                        colors = masterDesignArgs.getTextFieldColors(),
                                        shape =
                                            RoundedCornerShape(
                                                moduleDesignArgs.mShapeCard
                                                    ?: masterDesignArgs.mShapeCard,
                                            ),
                                        label = {
                                            Text(
                                                text = stringResource(id = R.string.public_transport_search_stop),
                                                color = masterDesignArgs.mHintTextColor,
                                                style = masterDesignArgs.normalTextStyle,
                                            )
                                        },
                                    )
                                }
                            }
                        }

                        Row(
                            Modifier.background(
                                Color.LightGray,
                                RoundedCornerShape(
                                    moduleDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard,
                                ),
                            ),
                        ) {
                            Button(
                                shape =
                                    RoundedCornerShape(
                                        moduleDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard,
                                    ),
                                elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                                onClick = {
                                    publicTransportViewModel.checkedStateOrigin.value = true
                                },
                                modifier =
                                    Modifier
                                        .padding(horizontal = 0.dp, vertical = 0.dp)
                                        .fillMaxWidth(0.5f),
                                colors =
                                    if (publicTransportViewModel.checkedStateOrigin.value) {
                                        if (moduleDesignArgs.mButtonBackgroundColor !=
                                            null
                                        ) {
                                            moduleDesignArgs.getButtonColors()
                                        } else {
                                            masterDesignArgs.getButtonColors()
                                        }
                                    } else {
                                        ButtonDefaults.buttonColors(
                                            backgroundColor = Color.LightGray,
                                            contentColor = Color.White,
                                        )
                                    },
                            ) {
                                ResponsiveText(
                                    text = stringResource(id = R.string.public_transport_search_start).uppercase(),
                                    color =
                                        moduleDesignArgs.mDialogsTextColor
                                            ?: masterDesignArgs.mDialogsTextColor,
                                    textStyle = masterDesignArgs.overlineTextStyle,
                                    maxLines = 1,
                                )
                            }

                            Button(
                                shape =
                                    RoundedCornerShape(
                                        moduleDesignArgs.mShapeCard ?: masterDesignArgs.mShapeCard,
                                    ),
                                elevation = ButtonDefaults.elevation(0.dp, 0.dp),
                                onClick = {
                                    publicTransportViewModel.checkedStateOrigin.value = false
                                },
                                modifier =
                                    Modifier
                                        .padding(horizontal = 0.dp, vertical = 0.dp)
                                        .fillMaxWidth(),
                                colors =
                                    if (!publicTransportViewModel.checkedStateOrigin.value) {
                                        if (moduleDesignArgs.mButtonBackgroundColor !=
                                            null
                                        ) {
                                            moduleDesignArgs.getButtonColors()
                                        } else {
                                            masterDesignArgs.getButtonColors()
                                        }
                                    } else {
                                        ButtonDefaults.buttonColors(
                                            backgroundColor = Color.LightGray,
                                            contentColor = Color.White,
                                        )
                                    },
                            ) {
                                ResponsiveText(
                                    text = stringResource(id = R.string.public_transport_search_stop).uppercase(),
                                    color =
                                        moduleDesignArgs.mDialogsTextColor
                                            ?: masterDesignArgs.mDialogsTextColor,
                                    textStyle = masterDesignArgs.overlineTextStyle,
                                    maxLines = 1,
                                )
                            }
                        }

                        DateTimePickerElement(
                            dateTimeString = getDateTimeAsString(dateTime.value),
                            onClickDate = {
                                datePickerState.show()
                            },
                            onClickTime = {
                                timePickerState.show()
                            },
                            masterDesignArgs = masterDesignArgs,
                            moduleDesignArgs = moduleDesignArgs,
                        )

                        MainButton(
                            buttonText = stringResource(id = R.string.public_transport_showRoute),
                            onClick = {
                                safeLet(origin.value, destination.value) { fromStop, toStop ->
                                    TripTempData.routeStopsData.clear()
                                    publicTransportViewModel.initializeRouteDate(
                                        fromStop.properties?.stopId ?: fromStop.id,
                                        toStop.properties?.stopId ?: toStop.id,
                                        dateTime.value,
                                    )

                                    publicTransportViewModel.screenContentLoading.value = true
                                }
                            },
                            masterDesignArgs = masterDesignArgs,
                            moduleDesignArgs = moduleDesignArgs,
                        )
                    }
                }
            }

            if (TripTempData.routeStopsData.isNotEmpty()) {
                publicTransportViewModel.screenContentLoading.value = false
                // Log.e("PUBTRANS A", TripTempData.routeStopsData.size.toString())

                for (route in TripTempData.routeStopsData) {
                    // Log.e("PUBTRANS B", route.originName + " - T = " + route.minutesUntilDeparture)

                    if (route.minutesUntilDeparture > 0) {
                        // Log.e("PUBTRANS C", route.originName)

                        item {
                            BaseCardContainer(
                                onClick = {
                                    navController
                                        .setOnCurrentState(BUNDLE_KEY_TRIP, route)
                                        .navigate(PublicTransportNavItems.RouteDetails)
                                },
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = moduleDesignArgs,
                            ) {
                                Column {
                                    LazyRow(
                                        modifier =
                                            Modifier
                                                .padding(bottom = 8.dp),
                                    ) {
                                        for (commute in route.getAllTransportations()) {
                                            item {
                                                CommuteButton(
                                                    commuteName = commute?.number.toString(),
                                                    iconId =
                                                        commute?.product?.getIconViaIconId()
                                                            ?: R.drawable.ic_bus,
                                                    masterDesignArgs = masterDesignArgs,
                                                )

                                                Spacer(
                                                    modifier =
                                                        Modifier
                                                            .width(8.dp),
                                                )
                                            }
                                        }
                                    }

                                    val untilDepartureString =
                                        if (route.minutesUntilDeparture == 0L) {
                                            stringResource(id = R.string.public_transport_departure_now)
                                        } else {
                                            stringResource(
                                                id = R.string.public_transport_departure_in_x_minutes,
                                                route.minutesUntilDeparture,
                                            )
                                        }

                                    Text(
                                        text = "Abfahrt: ${route.estimatedDepartureTime.toDateTimeString()}",
                                        style = masterDesignArgs.normalTextStyle,
                                        color = masterDesignArgs.mCardTextColor,
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                if (publicTransportViewModel.screenContentLoading.value) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(bottom = 16.dp),
                            )
                        }
                    }
                } else {
                    for (stop in publicTransportViewModel.nearbyStops) {
                        item {
                            BaseCardContainer(
                                onClick = {
                                    origin.value = stop.toStopResponse()
                                },
                                masterDesignArgs = masterDesignArgs,
                                moduleDesignArgs = moduleDesignArgs,
                            ) {
                                CommuteOptionItem(
                                    stopsNearByResponse = stop,
                                    masterDesignArgs = masterDesignArgs,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommuteOptionItem(
    stopsNearByResponse: StopsNearbyResponse,
    masterDesignArgs: MasterDesignArgs,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.haltestelle),
            contentDescription = "",
            modifier =
                Modifier
                    .size(35.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = stopsNearByResponse.name,
                style = masterDesignArgs.overlineTextStyle,
                color = masterDesignArgs.mCardTextColor,
                modifier =
                    Modifier
                        .padding(end = 12.dp),
            )

            Text(
                text = "${stopsNearByResponse.distance}m",
                style = masterDesignArgs.normalTextStyle,
                color = masterDesignArgs.mCardTextColor,
            )
        }
    }
}

@Composable
fun getDateTimeAsString(dateTime: LocalDateTime): String {
    return when {
        dateTime.toLocalDate().isToday() ->
            stringResource(
                id = R.string.essentials_value_date_time_today,
                dateTime.toTimeString() ?: "",
            )

        dateTime.toLocalDate().isTomorrow() ->
            stringResource(
                id = R.string.essentials_value_date_time_tomorrow,
                dateTime.toTimeString() ?: "",
            )

        else ->
            stringResource(
                id = R.string.essentials_value_date_time,
                dateTime.toDateString() ?: "",
                dateTime.toTimeString() ?: "",
            )
    }
}
