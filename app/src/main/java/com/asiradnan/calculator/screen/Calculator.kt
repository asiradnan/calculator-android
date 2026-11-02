package com.asiradnan.calculator.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.InterceptPlatformTextInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezylang.evalex.Expression
import kotlinx.coroutines.awaitCancellation
import java.math.RoundingMode

const val ops = "+-×÷=%"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Calculator(modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = "", selection = TextRange(0)))
    }
    var result by remember { mutableStateOf("") }
    var openBracketCount = 0;
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier) {
        Row(
            Modifier
                .fillMaxHeight(0.34f)
                .fillMaxWidth(1f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                InterceptPlatformTextInput(
                    interceptor = { _, _ ->
                        awaitCancellation()
                    }
                ) {
                    BoxWithConstraints(modifier = Modifier.fillMaxHeight(0.5f)) {
                        val textMeasurer = rememberTextMeasurer()
                        val density = LocalDensity.current

                        // Dynamically scale font size down as text gets longer
                        val fontSize = remember(textFieldValue.text, maxWidth) {
                            var size = 80.sp
                            val minSize = 28.sp
                            val maxPx = with(density) { maxWidth.toPx() }

                            if (textFieldValue.text.isNotEmpty()) {
                                while (size > minSize) {
                                    val textWidth = textMeasurer.measure(
                                        text = textFieldValue.text,
                                        style = TextStyle(fontSize = size)
                                    ).size.width

                                    if (textWidth < maxPx * 0.9) break
                                    size = (size.value - 4).sp // Decrease step size
                                }
                            }
                            size
                        }

                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            value = textFieldValue,
                            onValueChange = { newValue -> textFieldValue = newValue },
                            textStyle = TextStyle(
                                fontSize = fontSize,
                                textAlign = TextAlign.Right,
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                errorContainerColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
                Text(
                    text = result,
                    Modifier.fillMaxWidth(0.9f),
                    textAlign = TextAlign.Right,
//                    color = Color(0xFF6C6C70)
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    softWrap = false,
                    autoSize = TextAutoSize.StepBased(minFontSize = 10.sp, maxFontSize = 36.sp),
                )
            }
        }

        KeyPad(keyPress = { text ->
            var currentText = textFieldValue.text
            when (text) {
                "AC" -> {
                    currentText = ""
                    result = ""
                    openBracketCount = 0
                }

                "BACKSPACE" -> {
                    val lastChar = currentText.lastOrNull()
                    if (lastChar == '(') openBracketCount -= 1
                    else if (lastChar == ')') openBracketCount += 1
                    currentText = currentText.dropLast(1)
                }

                "()" -> {
                    if (openBracketCount == 0 || currentText.last() == '(' || currentText.last() in ops) {
                        currentText += '('
                        openBracketCount += 1
                    } else {
                        currentText += ')'
                        openBracketCount -= 1
                    }
                }

                "=" -> {
                    if (openBracketCount == 0) {
                        val cleanedText = currentText.replace('×', '*').replace('÷', '/')
                        result = Expression(cleanedText)
                            .evaluate()
                            .numberValue
                            .setScale(8, RoundingMode.HALF_UP)
                            .stripTrailingZeros()
                            .toString()
                    }

                }

                else -> currentText += text[0]
            }

            textFieldValue = TextFieldValue(
                text = currentText,
                selection = TextRange(currentText.length)
            )
        })
    }
}

@Composable
fun KeyPad(modifier: Modifier = Modifier, keyPress: (text: String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(vertical = 2.dp),
//            .background(color = Color.White),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        KeyPadRow(
            firstText = "AC",
            secondText = "()",
            thirdText = "%",
            fourthText = "÷",
            buttonClick = keyPress
        )
        KeyPadRow(
            firstText = "7",
            secondText = "8",
            thirdText = "9",
            fourthText = "×",
            buttonClick = keyPress
        )
        KeyPadRow(
            firstText = "4", secondText = "5", thirdText = "6",
            fourthText = "-",
            buttonClick = keyPress
        )
        KeyPadRow(
            firstText = "1", secondText = "2", thirdText = "3",
            fourthText = "+",
            buttonClick = keyPress
        )
        LastRow(buttonClick = keyPress)
    }
}

@Composable
fun KeyPadRow(
    modifier: Modifier = Modifier,
    firstText: String = "",
    secondText: String = "",
    thirdText: String = "",
    fourthText: String = "",
    buttonClick: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val modifier: Modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
        Key(
            modifier = modifier,
            text = firstText,
            buttonClick = { buttonClick(firstText) }
        )
        Key(
            modifier = modifier,
            text = secondText,
            buttonClick = { buttonClick(secondText) }
        )
        Key(
            modifier = modifier,
            text = thirdText,
            buttonClick = { buttonClick(thirdText) }
        )
        Key(
            modifier = modifier,
            text = fourthText,
            buttonClick = { buttonClick(fourthText) }
        )
    }
}

@Composable
fun LastRow(
    modifier: Modifier = Modifier,
    firstText: String = "0",
    secondText: String = ".",
    thirdIcon: ImageVector = Icons.AutoMirrored.Filled.Backspace,
    fourthText: String = "=",
    buttonClick: (String) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),

        ) {
        val modifier: Modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
        Key(
            modifier = modifier,
            text = firstText,
            buttonClick = { buttonClick(firstText) }
        )
        Key(
            modifier = modifier,
            text = secondText,
            buttonClick = { buttonClick(secondText) }
        )
        Button(
            onClick = { buttonClick("BACKSPACE") },
            modifier = modifier,
        ) {
            Icon(thirdIcon, contentDescription = null)
        }
        Key(
            modifier = modifier,
            text = fourthText,
            buttonClick = { buttonClick(fourthText) }
        )
    }
}

@Composable
fun Key(modifier: Modifier = Modifier, text: String, buttonClick: () -> Unit) {
    Button(
        onClick = buttonClick,
        modifier = modifier
    ) {
        Text(text = text, fontSize = if (text in ops || text == "()") 40.sp else 28.sp)
    }
}
