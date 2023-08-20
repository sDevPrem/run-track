package com.sdevprem.runtrack.ui.utils.component

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.core.data.model.Gender
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.utils.RunUtils.getDisplayDate
import java.util.Date

@Composable
fun UserProfilePic(
    modifier: Modifier = Modifier,
    imgUri: Uri?,
    gender: Gender,
    tint: Color = MaterialTheme.colorScheme.onPrimary
) {
    AsyncImage(
        model = imgUri,
        contentDescription = null,
        modifier = modifier,
        fallback = painterResource(
            id =
            if (gender == Gender.MALE)
                R.drawable.ic_male
            else
                R.drawable.ic_female
        ),
        contentScale = ContentScale.Crop,
        colorFilter = if (imgUri == null)
            ColorFilter.tint(color = tint)
        else null
    )
}

@Composable
fun RunningStatsItem(
    modifier: Modifier = Modifier,
    painter: Painter,
    unit: String,
    value: String
) {
    Row(modifier = modifier.padding(4.dp)) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(20.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(
            modifier = Modifier
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = unit,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun RunItem(
    modifier: Modifier = Modifier,
    run: Run,
    showTrailingIcon: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Image(
            bitmap = run.img.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(70.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.size(16.dp))
        RunInfo(
            modifier = Modifier
                .weight(1f),
            run = run
        )
        if (showTrailingIcon)
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = "More info",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.CenterVertically)
            )
    }
}

@Composable
private fun RunInfo(
    modifier: Modifier = Modifier,
    run: Run
) {
    Column(modifier) {
        Text(
            text = run.timestamp.getDisplayDate(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Normal
            ),
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = "${(run.distanceInMeters / 1000f)} km",
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.size(12.dp))
        Row {
            Text(
                text = "${run.caloriesBurned} kcal",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                ),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "${run.avgSpeedInKMH} km/hr",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                ),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun RunItemPrev() {
    RunItem(
        run = Run(
            img = BitmapFactory.decodeResource(
                LocalContext.current.resources,
                R.drawable.running_boy
            ),
            timestamp = Date(),
            avgSpeedInKMH = 13.56f,
            distanceInMeters = 10120,
            caloriesBurned = 701
        )
    )
}

@Composable
fun <T : Any> DropDownList(
    modifier: Modifier = Modifier,
    list: List<T>,
    stringTransform: (T) -> String = { it.toString() },
    onItemSelected: (T) -> Unit,
    isOpened: Boolean = false,
    request: (Boolean) -> Unit,
) {
    DropdownMenu(
        modifier = modifier,
        expanded = isOpened,
        onDismissRequest = { request(false) },
    ) {
        list.forEach {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringTransform(it),
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.Start)
                    )
                },
                onClick = { onItemSelected(it) },
            )
        }
    }
}