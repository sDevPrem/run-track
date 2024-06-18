package com.sdevprem.runtrack.ui.common.compose.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.common.extension.getDisplayDate
import com.sdevprem.runtrack.common.utils.DateTimeUtils
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.ui.theme.md_theme_light_onSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunInfoDialog(
    run: Run,
    onDismiss: () -> Unit,
    onDelete: (Run) -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.large
            ),
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .clip(
                    shape = MaterialTheme.shapes.large
                )
        ) {
            RunImage(
                run = run,
                onDismiss = onDismiss,
                onDelete = onDelete
            )
            Spacer(modifier = Modifier.size(24.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = run.timestamp.getDisplayDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = DateTimeUtils.getFormattedStopwatchTime(run.durationInMillis),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            RunStats(
                run = run,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp)
            )
        }
    }
}

@Composable
private fun RunImage(
    modifier: Modifier = Modifier,
    run: Run,
    onDismiss: () -> Unit,
    onDelete: (Run) -> Unit
) {
    Box(
        modifier = modifier,
    ) {
        Image(
            bitmap = run.img.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop,
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "close run info",
                modifier = Modifier,
                tint = md_theme_light_onSurface
            )

        }
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 8.dp)
        ) {
            IconButton(
                onClick = { onDelete(run) },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete run info",
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )

            }
            Spacer(modifier = Modifier.size(4.dp))
            IconButton(
                onClick = { },
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "share run info",
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

            }
        }
    }
}

@Composable
private fun RunStats(
    modifier: Modifier = Modifier,
    run: Run
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)

    ) {
        RunningStatsItem(
            modifier = Modifier,
            painter = painterResource(id = R.drawable.running_boy),
            unit = "km",
            value = (run.distanceInMeters / 1000f).toString()
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .padding(vertical = 8.dp)
                .align(Alignment.CenterVertically)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
        )
        RunningStatsItem(
            modifier = Modifier,
            painter = painterResource(id = R.drawable.fire),
            unit = "kcal",
            value = run.caloriesBurned.toString()
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .padding(vertical = 8.dp)
                .align(Alignment.CenterVertically)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
        )
        RunningStatsItem(
            modifier = Modifier,
            painter = painterResource(id = R.drawable.bolt),
            unit = "km/hr",
            value = run.avgSpeedInKMH.toString()
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun RunInfoDialogPreview() {
    RunInfoDialog(
        run = Run(
            img = BitmapFactory.decodeResource(
                LocalContext.current.resources,
                R.drawable.running_boy
            ),
            avgSpeedInKMH = 15.8f,
            distanceInMeters = 2500,
            durationInMillis = 5_400_000,
            caloriesBurned = 2423,
        ),
        onDelete = {},
        onDismiss = {}
    )
}