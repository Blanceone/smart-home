package com.smarthome

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smarthome.presentation.common.components.SmartTextField
import com.smarthome.presentation.common.components.SmartPasswordTextField
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InputFieldsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun smartTextField_shouldDisplayLabel() {
        val label = "用户名"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartTextField(
                        value = "",
                        onValueChange = {},
                        label = label
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(label).assertExists()
    }

    @Test
    fun smartTextField_shouldDisplayValue() {
        val value = "测试用户"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartTextField(
                        value = value,
                        onValueChange = {},
                        label = "用户名"
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(value).assertExists()
    }

    @Test
    fun smartTextField_shouldDisplayPlaceholder() {
        val placeholder = "请输入用户名"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartTextField(
                        value = "",
                        onValueChange = {},
                        label = "用户名",
                        placeholder = placeholder
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(placeholder).assertExists()
    }

    @Test
    fun smartTextField_shouldDisplayError() {
        val errorMessage = "用户名不能为空"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartTextField(
                        value = "",
                        onValueChange = {},
                        label = "用户名",
                        isError = true,
                        errorMessage = errorMessage
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertExists()
    }

    @Test
    fun smartPasswordTextField_shouldDisplayLabel() {
        val label = "密码"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartPasswordTextField(
                        value = "",
                        onValueChange = {},
                        label = label
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(label).assertExists()
    }

    @Test
    fun smartPasswordTextField_shouldDisplayPlaceholder() {
        val placeholder = "请输入密码"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartPasswordTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = placeholder
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(placeholder).assertExists()
    }
}
