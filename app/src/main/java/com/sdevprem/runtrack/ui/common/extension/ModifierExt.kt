package com.sdevprem.runtrack.ui.common.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = drawBehind {
    val width = size.width
    val height = size.height

    drawLine(
        color = color,
        start = Offset(x = 0f, y = height),
        end = Offset(x = width, y = height),
        strokeWidth = strokeWidth.toPx()
    )
}

fun Modifier.conditional(
    condition: Boolean,
    ifFalse: () -> Modifier = { Modifier },
    ifTrue: () -> Modifier
): Modifier = run {
    if (condition) then(ifTrue())
    else then(ifFalse())
}
