package com.sdevprem.runtrack.ui.utils

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.core.data.model.Gender

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