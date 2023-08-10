package com.sdevprem.runtrack.ui.screen.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.core.data.model.User
import com.sdevprem.runtrack.ui.common.RunningStatsItem
import com.sdevprem.runtrack.ui.utils.UserProfilePic
import com.sdevprem.runtrack.ui.utils.bottomBorder

@Composable
fun ProfileScreen(
    bottomPadding: Dp = 0.dp
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()
    val state by viewModel.profileScreenState.collectAsStateWithLifecycle()
    ProfileScreenContent(
        bottomPadding = bottomPadding,
        profileScreenState = state,
        onDoneButtonClick = viewModel::saveUser,
        onEditButtonClick = viewModel::startEditing,
        onUserNameChange = viewModel::updateUserName,
        onImgUriSelected = viewModel::updateImgUri
    )

    LaunchedEffect(key1 = state.errorMsg) {
        if (state.errorMsg.isNullOrBlank().not())
            Toast.makeText(context, state.errorMsg.toString(), Toast.LENGTH_SHORT).show()
    }
}

@Composable
private fun ProfileScreenContent(
    bottomPadding: Dp = 0.dp,
    profileScreenState: ProfileScreenState,
    onEditButtonClick: () -> Unit,
    onDoneButtonClick: () -> Unit,
    onUserNameChange: (String) -> Unit,
    onImgUriSelected: (Uri?) -> Unit
) {
    Column {
        TopBar(
            state = profileScreenState,
            onEditButtonClick = onEditButtonClick,
            onDoneButtonClick = onDoneButtonClick,
            onUserNameChange = onUserNameChange,
            onImgUriSelected = onImgUriSelected
        )
        Column(
            modifier = Modifier
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                ) {
                    SettingsItem(
                        img = painterResource(id = R.drawable.ic_raising_hand),
                        title = "Personal Parameter"
                    )
                    SettingsItem(
                        img = painterResource(id = R.drawable.ic_trophy),
                        title = "Achievements"
                    )
                    SettingsItem(
                        img = painterResource(id = R.drawable.ic_gear),
                        title = "Settings"
                    )
                    SettingsItem(
                        img = painterResource(id = R.drawable.ic_telephone_receiver),
                        title = "Our Contact",
                        showDivider = false
                    )
                }
            }
            Spacer(modifier = Modifier.size(bottomPadding + 8.dp))
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    state: ProfileScreenState,
    onDoneButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    onUserNameChange: (String) -> Unit,
    onImgUriSelected: (Uri?) -> Unit
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
                modifier = Modifier.background(color = Color.Transparent),
                onDoneButtonClick = onDoneButtonClick,
                onEditButtonClick = onEditButtonClick,
                user = state.user,
                isEditMode = state.isEditMode,
                onUserNameChange = onUserNameChange,
                onImgUriSelected = onImgUriSelected
            )
            Spacer(modifier = Modifier.size(32.dp))
            TotalProgressCard(state = state)
        }
    }

}

@Composable
private fun TopBarProfile(
    modifier: Modifier = Modifier,
    user: User,
    isEditMode: Boolean,
    onEditButtonClick: () -> Unit,
    onDoneButtonClick: () -> Unit,
    onUserNameChange: (String) -> Unit,
    onImgUriSelected: (Uri?) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { it?.let(onImgUriSelected) }
    )
    val userNameFocusRequester = remember { FocusRequester() }
    Box() {
        Column(
            modifier = modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.size(24.dp))
            Box {

                UserProfilePic(
                    imgUri = user.imgUri,
                    gender = user.gender,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(CircleShape)
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = isEditMode,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                ) {
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .requiredSize(24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_edit),
                            contentDescription = "Change Photo",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .size(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(12.dp))

            val userNameStyle = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            if (!isEditMode)
                Text(
                    text = user.name,
                    style = userNameStyle
                )
            else
                BasicTextField(
                    value = user.name,
                    onValueChange = onUserNameChange,
                    textStyle = userNameStyle,
                    modifier = Modifier
                        .focusRequester(userNameFocusRequester)
                        .wrapContentHeight()
                        .width(200.dp)
                        .padding(bottom = 4.dp)
                        .bottomBorder(1.dp, MaterialTheme.colorScheme.onPrimary),
                    maxLines = 1,
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary)
                )
            AnimatedVisibility(
                visible = isEditMode && user.imgUri != null,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                OutlinedButton(
                    onClick = { onImgUriSelected(null) },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(MaterialTheme.colorScheme.onPrimary)
                    )
                ) {
                    Text(
                        text = "Remove Picture",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        IconButton(
            onClick = if (!isEditMode) onEditButtonClick else onDoneButtonClick,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = if (!isEditMode)
                    ImageVector.vectorResource(id = R.drawable.ic_edit)
                else
                    Icons.Default.Done,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        LaunchedEffect(key1 = isEditMode) {
            if (isEditMode)
                userNameFocusRequester.requestFocus()
        }
    }
}

@Composable
private fun TotalProgressCard(
    modifier: Modifier = Modifier,
    state: ProfileScreenState,
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Total Progress",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_forward),
                contentDescription = "More info",
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RunningStatsItem(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.running_boy),
                unit = "km",
                value = state.totalDistanceInKm.toString()
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
                painter = painterResource(id = R.drawable.stopwatch),
                unit = "hr",
                value = state.totalDurationInHr.toString()
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
                value = state.totalCaloriesBurnt.toString()
            )
        }
    }
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    img: Painter,
    title: String,
    showDivider: Boolean = true
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Image(
                painter = img,
                contentDescription = title,
                modifier = Modifier
                    .size(32.dp),
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .weight(1f)
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_forward),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
        if (showDivider)
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .width(200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.2f
                        )
                    )
                    .align(Alignment.CenterHorizontally)
            )
    }
}
