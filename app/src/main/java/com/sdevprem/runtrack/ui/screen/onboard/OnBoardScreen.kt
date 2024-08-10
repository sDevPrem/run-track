package com.sdevprem.runtrack.ui.screen.onboard

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sdevprem.runtrack.R
import com.sdevprem.runtrack.data.model.Gender
import com.sdevprem.runtrack.data.model.User
import com.sdevprem.runtrack.ui.common.compose.component.UserProfilePic
import com.sdevprem.runtrack.ui.nav.Destination
import kotlinx.coroutines.launch

private const val TOTAL_PAGE = 2

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardScreen(
    navController: NavController,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { TOTAL_PAGE })
    val coroutineScope = rememberCoroutineScope()

    BackHandler(true) {
        if (pagerState.currentPage > 0) coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage - 1)
        } else
        //if user wants to exit this screen
        //exit the app :)
            (context as? Activity)?.finish()
    }

    OnBoardScreenContent(
        user,
        viewModel,
        pagerState = pagerState
    ) {
        viewModel.saveUser {
            Destination.OnBoardingDestination.navigateToHome(navController)
        }
    }

    LaunchedEffect(key1 = viewModel.errorMsg.value) {
        if (viewModel.errorMsg.value.isNotBlank()) {
            Toast.makeText(context, viewModel.errorMsg.value, Toast.LENGTH_SHORT).show()
            viewModel.resetErrorMsg()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnBoardScreenContent(
    user: User,
    onBoardingScreenEvent: OnBoardingScreenEvent,
    pagerState: PagerState,
    onSave: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pageModifier = Modifier.padding(horizontal = 24.dp)

    Column {
        OnBoardingScreenHeader(
            modifier = pageModifier
                .weight(0.8f)
        )
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> FirstPage(
                    modifier = pageModifier.fillMaxHeight(),
                    name = user.name,
                    gender = user.gender,
                    onNameChange = onBoardingScreenEvent::updateName,
                    onGenderChange = onBoardingScreenEvent::updateGender,
                    onUserImageUriChange = onBoardingScreenEvent::updateUserImgUri,
                    userImgUri = user.imgUri
                )

                1 -> SecondPage(
                    modifier = pageModifier.fillMaxHeight(),
                    weightInKg = user.weightInKg,
                    weeklyGoal = user.weeklyGoalInKM,
                    onWeeklyGoalChange = onBoardingScreenEvent::updateWeeklyGoal,
                    onWeightChange = onBoardingScreenEvent::updateWeight
                )
            }
        }
        BottomItem(
            selectedPage = pagerState.currentPage,
            onNextButtonClicked = {
                if (pagerState.currentPage < TOTAL_PAGE - 1) coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
                if (pagerState.currentPage == TOTAL_PAGE - 1) {
                    onSave()
                }
            },
            isLast = pagerState.currentPage == TOTAL_PAGE - 1,
            onBackButtonClicked = {
                if (pagerState.currentPage > 0) coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            }
        )
    }

}

@Composable
private fun OnBoardingScreenHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_run),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(64.dp)
        )
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Let's run together",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun SecondPage(
    modifier: Modifier = Modifier,
    weightInKg: Float,
    weeklyGoal: Float,
    onWeightChange: (Float) -> Unit,
    onWeeklyGoalChange: (Float) -> Unit
) = Column(
    modifier = modifier
) {

    OutlinedTextField(
        value = if (weightInKg > 0) weightInKg.toString() else "",
        onValueChange = { if (it.isNotBlank()) onWeightChange(it.toFloat()) },
        label = { Text(text = "Weight") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        suffix = { Text(text = "kg") }
    )

    Spacer(modifier = Modifier.size(16.dp))

    OutlinedTextField(
        value = if (weeklyGoal > 0) weeklyGoal.toString() else "",
        onValueChange = { if (it.isNotBlank()) onWeeklyGoalChange(it.toFloat()) },
        label = { Text(text = "Weekly target") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        suffix = { Text(text = "km") },
    )
}

@Composable
private fun FirstPage(
    modifier: Modifier = Modifier,
    name: String,
    gender: Gender,
    userImgUri: Uri?,
    onNameChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onUserImageUriChange: (Uri?) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UserImage(
            selectedGender = gender,
            userImgUri = userImgUri,
            setUserImageUri = onUserImageUriChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = {
                    Text(text = "Name")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            GenderCard(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f),
                selectedGender = gender,
                cardGender = Gender.MALE,
                onGenderChange = onGenderChange
            )
            GenderCard(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                selectedGender = gender,
                cardGender = Gender.FEMALE,
                onGenderChange = onGenderChange
            )
        }
    }
}

@Composable
private fun UserImage(
    selectedGender: Gender,
    setUserImageUri: (ur: Uri?) -> Unit,
    userImgUri: Uri?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let {
                context.contentResolver
                    .takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            setUserImageUri(it)
        }
    )
    Box(
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = userImgUri ?: selectedGender,
            label = "UserImageAnimation",
        ) {
            UserProfilePic(
                imgUri = it as? Uri,
                gender = selectedGender,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd),
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary
            ),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_edit),
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun GenderCard(
    modifier: Modifier,
    selectedGender: Gender,
    cardGender: Gender,
    onGenderChange: (Gender) -> Unit
) {
    val isSelected = cardGender == selectedGender
    OutlinedCard(
        onClick = { onGenderChange(cardGender) },
        modifier = modifier,
        border = BorderStroke(2.dp, getGenderCardColor(isSelected))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            GenderCardCheckBox(
                isSelected = isSelected,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 8.dp)
            )
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = when (cardGender) {
                        Gender.MALE -> R.drawable.ic_male
                        Gender.FEMALE -> R.drawable.ic_female
                    }
                ),
                contentDescription = "Female",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                tint = getGenderCardColor(isSelected = isSelected)
            )
        }
    }
}

@Composable
private fun GenderCardCheckBox(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .border(
                width = 2.dp,
                color = getGenderCardColor(isSelected = isSelected),
                shape = CircleShape
            )
            .padding(4.dp)
    ) {
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun getGenderCardColor(isSelected: Boolean) =
    if (isSelected)
        MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

@Composable
@Preview(showBackground = true)
private fun BottomItem(
    onNextButtonClicked: () -> Unit = {},
    onBackButtonClicked: () -> Unit = {},
    selectedPage: Int = 0,
    isLast: Boolean = false
) {
    val containerColor = MaterialTheme.colorScheme.primary
    val contentColor = MaterialTheme.colorScheme.onPrimary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        AnimatedVisibility(
            visible = isLast,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            IconButton(
                onClick = { onBackButtonClicked() },
                modifier = Modifier
                    .border(
                        shape = MaterialTheme.shapes.medium,
                        color = containerColor,
                        width = 1.dp
                    ),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = containerColor,
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_backward),
                    contentDescription = "back",
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onNextButtonClicked() }
                .background(
                    color = containerColor,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = isLast) {
                Text(
                    text = "Get Started",
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )

            }
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_forward),
                contentDescription = "next",
                tint = contentColor,
                modifier = Modifier
                    .size(16.dp)

            )
        }
    }
}