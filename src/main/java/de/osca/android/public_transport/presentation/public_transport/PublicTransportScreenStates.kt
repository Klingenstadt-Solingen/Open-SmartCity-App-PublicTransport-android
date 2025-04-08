package de.osca.android.public_transport.presentation.public_transport

import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusRequester
import com.vanpra.composematerialdialogs.MaterialDialogState
import de.osca.android.public_transport.domain.entity.StopType
import java.time.LocalDateTime

@OptIn(ExperimentalMaterialApi::class)
data class PublicTransportScreenStates constructor(
    val bottomSheetState: BottomSheetScaffoldState,
    val selectionStopType: MutableState<StopType>,
    val stopSearchFocusRequester: FocusRequester,
    val stopSearchValue: MutableState<String>,
    val datePickerState: MaterialDialogState,
    val timePickerState: MaterialDialogState,
    val desiredTripDateTime: MutableState<LocalDateTime>
)