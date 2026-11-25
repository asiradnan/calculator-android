package com.asiradnan.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.asiradnan.calculator.screen.CalculatorApp
import com.asiradnan.calculator.ui.theme.CalculatorTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalculatorUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        composeTestRule.setContent {
            var darkTheme by remember { mutableStateOf(false) }
            CalculatorTheme(darkTheme = darkTheme) {
                CalculatorApp(darkTheme = darkTheme, toggleTheme = { darkTheme = !darkTheme })
            }
        }
    }

    @Test
    fun calculate_addition() {
        composeTestRule.onNodeWithText("7").performClick()
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("8").performClick()
        composeTestRule.onNodeWithText("=").performClick()

        composeTestRule.onNodeWithTag("expression").assertTextEquals("7+8")
        composeTestRule.onNodeWithTag("result").assertTextEquals("15")
    }

    @Test
    fun calculate_subtraction_multiplication_division_percentage_and_decimals() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText(".").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("×").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("-").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("÷").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("=").performClick()

        composeTestRule.onNodeWithTag("expression").assertTextEquals("12.5×2-5÷2")
        composeTestRule.onNodeWithTag("result").assertTextEquals("22.5")

        // Clear and test percentage: 50% × 200 = 100
        composeTestRule.onNodeWithText("AC").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText("×").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("0").performClick()
        composeTestRule.onNodeWithText("%").performClick()
        composeTestRule.onNodeWithText("=").performClick()

        composeTestRule.onNodeWithTag("expression").assertTextEquals("50×50%")
        composeTestRule.onNodeWithTag("result").assertTextEquals("25")
    }

    @Test
    fun allClear_resetsExpressionAndResult() {
        composeTestRule.onNodeWithText("9").performClick()
        composeTestRule.onNodeWithText("×").performClick()
        composeTestRule.onNodeWithText("9").performClick()
        composeTestRule.onNodeWithText("=").performClick()

        composeTestRule.onNodeWithTag("expression").assertTextEquals("9×9")
        composeTestRule.onNodeWithTag("result").assertTextEquals("81")

        composeTestRule.onNodeWithText("AC").performClick()

        composeTestRule.onNodeWithTag("expression").assertTextEquals("0")
        composeTestRule.onNodeWithTag("result").assertTextEquals("")
    }

    @Test
    fun backspace_deletesLastCharacter() {
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("9").performClick()

        composeTestRule.onNodeWithContentDescription("Backspace").performClick()
        composeTestRule.onNodeWithText("4").performClick()
        composeTestRule.onNodeWithText("=").performClick()

        composeTestRule.onNodeWithTag("expression").assertTextEquals("123+4")
        composeTestRule.onNodeWithTag("result").assertTextEquals("127")
    }

    @Test
    fun parentheses_calculatesGroupedExpression() {
        // (7+8)×2 = 30
        composeTestRule.onNodeWithText("()").performClick()
        composeTestRule.onNodeWithText("7").performClick()
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("8").performClick()
        composeTestRule.onNodeWithText("()").performClick()
        composeTestRule.onNodeWithText("×").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("=").performClick()

        composeTestRule.onNodeWithTag("expression").assertTextEquals("(7+8)×2")
        composeTestRule.onNodeWithTag("result").assertTextEquals("30")
    }

    @Test
    fun themeToggle_switchesBetweenLightAndDarkMode() {
        composeTestRule.onNodeWithContentDescription("Light Mode").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Light Mode").performClick()

        composeTestRule.onNodeWithContentDescription("Dark Mode").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Dark Mode").performClick()

        composeTestRule.onNodeWithContentDescription("Light Mode").assertIsDisplayed()
    }
}
