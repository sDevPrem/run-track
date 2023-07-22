package com.sdevprem.runtrack.ui.screen.home

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.core.data.model.Run
import com.sdevprem.runtrack.core.tracking.model.CurrentRunState
import com.sdevprem.runtrack.ui.nav.Destination
import com.sdevprem.runtrack.utils.RunUtils
import com.sdevprem.runtrack.utils.RunUtils.formatToHomeRunItem
import java.util.Date

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp,
    navController: NavController
) {
    val runList by homeViewModel.runList.collectAsStateWithLifecycle()
    val currentRunState by homeViewModel.currentRunState.collectAsStateWithLifecycle()
    val durationInMillis by homeViewModel.durationInMillis.collectAsStateWithLifecycle()
    Column {
        TopBar(
            modifier = Modifier
                .zIndex(1f)
        )
        if (durationInMillis > 0)
            CurrentRunningCard(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 28.dp)
                    .clickable {
                        navController.navigate(Destination.CurrentRun.route)
                    },
                durationInMillis = durationInMillis,
                currentRunState = currentRunState,
            )
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(vertical = 28.dp, horizontal = 24.dp)
        ) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "All",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = bottomPadding + 8.dp)
                    .wrapContentHeight()
            ) {
                LazyColumn() {
                    item {
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                    val maxIndex = minOf(2/*runList.lastIndex*/, runList.lastIndex)
                    itemsIndexed(items = runList.subList(0, maxIndex + 1)) { i, run ->
                        Column {
                            RunItem(
                                run = run,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                            )
                            if (i < maxIndex)
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp)
                                        .height(1.dp)
                                        .width(200.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                        .align(Alignment.CenterHorizontally)
                                )
                            else
                                Spacer(modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(bottomPadding + 8.dp))
        }
    }


}

@Composable
private fun CurrentRunningCard(
    modifier: Modifier = Modifier,
    currentRunState: CurrentRunState = CurrentRunState(),
    durationInMillis: Long = 0
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(
                    topStartPercent = 50,
                    topEndPercent = 50,
                    bottomEndPercent = 50,
                    bottomStartPercent = 50
                )
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
        ) {
            Image(
                painter = painterResource(id = R.drawable.running_boy),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = 8.dp)
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = "Current Session",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = RunUtils.getFormattedStopwatchTime(durationInMillis),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
        Column(
            modifier = Modifier
        ) {
            Text(
                text = "${currentRunState.distanceInMeters / 1000f} km",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = "${currentRunState.caloriesBurnt} kcal",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun TopBar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .offset(y = (-24).dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)
                )
        )
        Column(modifier = modifier.padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.size(24.dp))
            TopBarProfile(
                modifier = Modifier.background(color = Color.Transparent)
            )
            Spacer(modifier = Modifier.size(32.dp))
            WeeklyGoalCard(
                weeklyGoalInKm = 50,
                weeklyGoalDoneInKm = 23.5f
            )
        }
    }

}

@Composable
private fun TopBarProfile(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.demo_profile_pic),
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentDescription = "User profile"
        )
        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = buildAnnotatedString {
                append("Hello, ")
                withStyle(
                    style = SpanStyle(fontWeight = FontWeight.SemiBold),
                ) {
                    append("Andrew")
                }
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .weight(1f)
        )

        IconButton(
            onClick = {},
            modifier = Modifier
                .size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_settings),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

@Composable
private fun WeeklyGoalCard(
    modifier: Modifier = Modifier,
    weeklyGoalInKm: Int,
    weeklyGoalDoneInKm: Float
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Weekly Goal",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "$weeklyGoalInKm km",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.arrow_toward_right),
                contentDescription = "More info",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.CenterVertically)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "$weeklyGoalDoneInKm km done",
                    modifier = Modifier
                        .weight(1f),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "${weeklyGoalInKm - weeklyGoalDoneInKm} km left",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
            Spacer(
                modifier = Modifier.size(8.dp)
            )
            LinearProgressIndicator(
                progress = if (weeklyGoalInKm > 0) weeklyGoalDoneInKm / weeklyGoalInKm else 0f,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }
    }

}

@Composable
private fun RunItem(
    modifier: Modifier = Modifier,
    run: Run
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Image(
            bitmap = run.img!!.asImageBitmap(),
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
        Image(
            painter = painterResource(id = R.drawable.arrow_toward_right),
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
            text = run.timestamp.formatToHomeRunItem(),
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