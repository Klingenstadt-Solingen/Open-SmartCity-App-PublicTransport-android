package de.osca.android.public_transport.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.osca.android.essentials.presentation.component.design.MasterDesignArgs
import de.osca.android.public_transport.R

@Composable
fun CommuteButton(
    commuteName: String,
    @DrawableRes iconId: Int = R.drawable.ic_directions_bus,
    masterDesignArgs: MasterDesignArgs
) {
    Card(
        shape = RoundedCornerShape(6.dp),
        elevation = 0.dp,
        backgroundColor = masterDesignArgs.mDialogsBackColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .padding(6.dp)
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp),
                tint = masterDesignArgs.mDialogsTextColor
            )

            Text(
                text = commuteName,
                style = masterDesignArgs.bodyTextStyle,
                color = masterDesignArgs.mDialogsTextColor,
                modifier = Modifier
                    .padding(start = 6.dp, end = 12.dp)
            )
        }
    }
}