package com.smarthome

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smarthome.presentation.common.components.LoadingIndicator
import com.smarthome.presentation.common.components.EmptyState
import com.smarthome.presentation.common.components.SmartButton
import com.smarthome.presentation.common.components.ButtonType
import com.smarthome.presentation.common.components.EmptyStateType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommonComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingIndicator_shouldDisplay() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    LoadingIndicator()
                }
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun emptyState_shouldDisplayMessage() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    EmptyState(
                        type = EmptyStateType.CUSTOM,
                        title = "暂无数据",
                        message = "当前没有可显示的内容"
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("暂无数据").assertExists()
    }

    @Test
    fun emptyState_shouldDisplayNoSchemesMessage() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    EmptyState(type = EmptyStateType.NO_SCHEMES)
                }
            }
        }

        composeTestRule.onNodeWithText("暂无方案").assertExists()
    }

    @Test
    fun emptyState_shouldDisplayNoNetworkMessage() {
        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    EmptyState(type = EmptyStateType.NO_NETWORK)
                }
            }
        }

        composeTestRule.onNodeWithText("网络异常").assertExists()
    }

    @Test
    fun smartButton_shouldDisplayText() {
        val buttonText = "点击我"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartButton(
                        text = buttonText,
                        onClick = {},
                        type = ButtonType.Primary
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(buttonText).assertExists()
    }

    @Test
    fun smartButton_shouldBeClickable() {
        var clicked = false
        val buttonText = "点击测试"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartButton(
                        text = buttonText,
                        onClick = { clicked = true },
                        type = ButtonType.Primary
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(buttonText).performClick()
        
        assert(clicked)
    }

    @Test
    fun smartButton_shouldDisplaySecondaryButton() {
        val buttonText = "次要按钮"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartButton(
                        text = buttonText,
                        onClick = {},
                        type = ButtonType.Secondary
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(buttonText).assertExists()
    }

    @Test
    fun smartButton_shouldDisplayOutlineButton() {
        val buttonText = "边框按钮"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartButton(
                        text = buttonText,
                        onClick = {},
                        type = ButtonType.Outline
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(buttonText).assertExists()
    }
}
