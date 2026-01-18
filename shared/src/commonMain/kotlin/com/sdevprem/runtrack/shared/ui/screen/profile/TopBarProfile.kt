package com.sdevprem.runtrack.shared.ui.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.toUri
import com.sdevprem.runtrack.shared.data.model.User
import com.sdevprem.runtrack.shared.ui.common.compose.components.UserProfilePic
import com.sdevprem.runtrack.shared.ui.common.extension.bottomBorder
import org.jetbrains.compose.resources.vectorResource
import runtrack.shared.generated.resources.Res
import runtrack.shared.generated.resources.ic_edit

@Composable
fun TopBarProfile(
    modifier: Modifier = Modifier,
    user: User,
    isEditMode: Boolean,
    profileEditActions: ProfileEditActions
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {

        TopBarProfileContent(
            user = user,
            isEditMode = isEditMode,
            profileEditActions = profileEditActions
        )

        AnimatedVisibility(
            visible = isEditMode,
            enter = scaleIn(),
            exit = scaleOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = profileEditActions::cancelEditing,
                modifier = Modifier
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Editing",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        IconButton(
            onClick = if (!isEditMode) profileEditActions::startEditing else profileEditActions::saveUser,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = if (!isEditMode)
                    vectorResource(Res.drawable.ic_edit)
                else
                    Icons.Default.Done,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

    }
}

@Composable
private fun TopBarProfileContent(
    modifier: Modifier = Modifier,
    user: User,
    isEditMode: Boolean,
    profileEditActions: ProfileEditActions
) {
    val userNameFocusRequester = remember { FocusRequester() }
//    val pickerState = rememberMediaPickerState()
//
//    MediaPicker(
//        state = pickerState,
//        onResult = { result ->
//            when (result) {
//                is MediaResult.Image -> { result }
//                else -> {}
//            }
//        },
//        onPermissionDenied = { deniedPermission -> }
//    )

    LaunchedEffect(key1 = isEditMode) {
        if (isEditMode)
            userNameFocusRequester.requestFocus()
    }

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
                imgUri = user.imgUri?.toUri(),
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
                    .offset(x = 8.dp, y = 8.dp)
            ) {
                IconButton(
                    onClick = {
//                        pickerState.pickImage(maxCount = 1)
                    },
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_edit),
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
                onValueChange = profileEditActions::updateUserName,
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

        AnimatedVisibility(isEditMode && user.imgUri != null) {
            Spacer(modifier = Modifier.size(4.dp))

            OutlinedButton(
                onClick = { profileEditActions.updateImgUri(null) },
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
}