package com.asiradnan.calculator.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ezylang.evalex.Expression
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

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Display Section
        Column(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Input Expression
            Text(
                text = textFieldValue.text.ifEmpty { "0" },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                softWrap = true,
                autoSize = TextAutoSize.StepBased(minFontSize = 24.sp, maxFontSize = 64.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Result Preview
            Text(
                text = result,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                softWrap = false,
                autoSize = TextAutoSize.StepBased(minFontSize = 14.sp, maxFontSize = 32.sp)
            )
        }

        Box(
            Modifier
                .weight(.66f)
                .fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            KeyPad(modifier = Modifier.aspectRatio(4f / 5f), keyPress = { text ->
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
                            try {
                                result = Expression(cleanedText).evaluate().numberValue.setScale(
                                    8,
                                    RoundingMode.HALF_UP
                                ).stripTrailingZeros().toPlainString()
                            } catch (e: Exception) {

                            }

                        }
                    }

                    else -> {
                        if (!(currentText.isNotEmpty() && currentText.last() in ops && text in ops))
                            currentText += text[0]
                    }
                }

                textFieldValue = TextFieldValue(
                    text = currentText, selection = TextRange(currentText.length)
                )
            })
        }
    }
}

@Composable
fun KeyPad(modifier: Modifier = Modifier, keyPress: (text: String) -> Unit) {
    Column(
        modifier = modifier.padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        KeyPadRow(
            modifier = Modifier.weight(1f),
            firstText = "AC",
            secondText = "()",
            thirdText = "%",
            fourthText = "÷",
            buttonClick = keyPress
        )
        KeyPadRow(
            modifier = Modifier.weight(1f),
            firstText = "7",
            secondText = "8",
            thirdText = "9",
            fourthText = "×",
            buttonClick = keyPress
        )
        KeyPadRow(
            modifier = Modifier.weight(1f),
            firstText = "4",
            secondText = "5",
            thirdText = "6",
            fourthText = "-",
            buttonClick = keyPress
        )
        KeyPadRow(
            modifier = Modifier.weight(1f),
            firstText = "1",
            secondText = "2",
            thirdText = "3",
            fourthText = "+",
            buttonClick = keyPress
        )
        LastRow(
            modifier = Modifier.weight(1f),
            buttonClick = keyPress
        )
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
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val keyModifier: Modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        Key(
            modifier = keyModifier, text = firstText, buttonClick = { buttonClick(firstText) })
        Key(
            modifier = keyModifier, text = secondText, buttonClick = { buttonClick(secondText) })
        Key(
            modifier = keyModifier, text = thirdText, buttonClick = { buttonClick(thirdText) })
        Key(
            modifier = keyModifier, text = fourthText, buttonClick = { buttonClick(fourthText) })
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
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val keyModifier: Modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
        Key(
            modifier = keyModifier, text = firstText, buttonClick = { buttonClick(firstText) })
        Key(
            modifier = keyModifier, text = secondText, buttonClick = { buttonClick(secondText) })
        Button(
            onClick = { buttonClick("BACKSPACE") },
            modifier = keyModifier,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = thirdIcon,
                contentDescription = "Backspace",
                modifier = Modifier.size(24.dp)
            )
        }
        Key(
            modifier = keyModifier, text = fourthText, buttonClick = { buttonClick(fourthText) })
    }
}

@Composable
fun Key(
    modifier: Modifier = Modifier,
    text: String,
    buttonClick: () -> Unit
) {
    Button(
        onClick = buttonClick,
        modifier = modifier,
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = if (text in ops || text == "()") 26.sp else 22.sp,
            maxLines = 1,
            softWrap = false
        )
    }
}