package de.osca.android.public_transport.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.essentials.utils.extensions.toTimeString
import de.osca.android.public_transport.R
import de.osca.android.public_transport.domain.entity.trip.Trip
import de.osca.android.public_transport.domain.entity.trip.TripInterchange

@Composable
fun RouteCommute(
    trip: Trip? = null,
    interchange: TripInterchange? = null,
    onClick: () -> Unit = { },
    masterDesignArgs: MasterDesignArgs
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (trip != null) 150.dp else 50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp),
                thickness = 2.dp,
                color = masterDesignArgs.mDialogsBackColor
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
                .clickable {
                    onClick()
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row {
                    Column(
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        if (trip != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if(trip.transportation != null && trip.transportation?.product != null) {
                                    CommuteButton(
                                        commuteName = trip.transportation!!.number,
                                        iconId = trip.transportation!!.product.getIconViaIconId(),
                                        masterDesignArgs = masterDesignArgs
                                    )
                                }
                            }

                            Text(
                                text = "nach: ${trip.transportation?.destination?.name}",
                                style = masterDesignArgs.bodyTextStyle,
                                color = masterDesignArgs.mCardTextColor
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "von: ${trip.origin?.name}",
                                style = masterDesignArgs.bodyTextStyle,
                                color = masterDesignArgs.mCardTextColor
                            )

                            if (trip.stopSequence.isNotEmpty()) {
                                Text(
                                    text = "${trip.stopSequence.size}" + stringResource(id = R.string.public_transport_stopCount),
                                    style = masterDesignArgs.normalTextStyle,
                                    color = masterDesignArgs.mCardTextColor
                                )
                            }

                            Text(
                                text = "bis: ${trip.destination?.name}",
                                style = masterDesignArgs.bodyTextStyle,
                                color = masterDesignArgs.mCardTextColor
                            )
                        } else if (interchange != null) {
                            Text(
                                text = interchange.name,
                                style = masterDesignArgs.normalTextStyle,
                                color = masterDesignArgs.mCardTextColor
                            )
                        }
                    }
                }

                if (trip != null) {
                    Column(
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${trip.estimatedDepartureTime().toTimeString()}" +
                                    stringResource(id = R.string.global_minute_abbreviation).lowercase(),
                            style = masterDesignArgs.normalTextStyle,
                            textAlign = TextAlign.End,
                            color = masterDesignArgs.mCardTextColor
                        )

                        Text(
                            text = "${trip.durationInMinutes}min" +
                                    stringResource(id = R.string.global_minute_abbreviation).lowercase(),
                            style = masterDesignArgs.overlineTextStyle,
                            textAlign = TextAlign.End,
                            color = masterDesignArgs.mCardTextColor
                        )
                    }
                }
            }
        }
    }
}