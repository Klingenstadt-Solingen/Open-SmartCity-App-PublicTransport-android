package de.osca.android.public_transport.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.utils.extensions.toTimeString
import de.osca.android.public_transport.R
import de.osca.android.public_transport.domain.entity.RouteResponse

@Composable
fun RouteStopRow(
    route: RouteResponse,
    isStart: Boolean,
    onClickStopRow: () -> Unit = { },
    masterDesignArgs: MasterDesignArgs
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.haltestelle),
                contentDescription = null,
                modifier = Modifier
                    .width(24.dp)
            )

            Column {
                Row(modifier = Modifier
                    .clickable {
                        onClickStopRow()
                    }
                ) {
                    Text(
                        text = if (isStart) route.originName else route.destinationName,
                        style = masterDesignArgs.normalTextStyle,
                        color = masterDesignArgs.mCardTextColor,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        maxLines = 2
                    )

                    Text(
                        text = if (isStart) {
                            route.estimatedDepartureTime.toTimeString() ?: ""
                        } else {
                            route.getRealArrivalTime().toTimeString() ?: ""
                        },
                        style = masterDesignArgs.normalTextStyle,
                        color = masterDesignArgs.mCardTextColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}