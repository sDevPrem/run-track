package com.sdevprem.runtrack.ui.utils

import android.net.Uri
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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