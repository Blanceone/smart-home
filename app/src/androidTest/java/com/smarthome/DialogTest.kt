package com.smarthome

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smarthome.presentation.common.components.SmartDialog
import com.smarthome.presentation.common.components.ConfirmDialog
import com.smarthome.presentation.common.components.DialogType
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun smartDialog_shouldDisplayTitle() {
        val title = "提示信息"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartDialog(
                        show = true,
                        onDismiss = {},
                        title = title,
                        type = DialogType.INFO
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(title).assertExists()
    }

    @Test
    fun smartDialog_shouldDisplayMessage() {
        val message = "这是一条提示信息"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartDialog(
                        show = true,
                        onDismiss = {},
                        title = "提示",
                        message = message,
                        type = DialogType.INFO
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(message).assertExists()
    }

    @Test
    fun smartDialog_shouldDisplayConfirmButton() {
        val confirmText = "确定"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartDialog(
                        show = true,
                        onDismiss = {},
                        title = "提示",
                        confirmText = confirmText
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(confirmText).assertExists()
    }

    @Test
    fun smartDialog_shouldDisplayBothButtons() {
        val confirmText = "确定"
        val dismissText = "取消"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartDialog(
                        show = true,
                        onDismiss = {},
                        title = "确认操作",
                        confirmText = confirmText,
                        dismissText = dismissText
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(confirmText).assertExists()
        composeTestRule.onNodeWithText(dismissText).assertExists()
    }

    @Test
    fun confirmDialog_shouldDisplayTitle() {
        val title = "确认删除"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    ConfirmDialog(
                        show = true,
                        onDismiss = {},
                        onConfirm = {},
                        title = title,
                        message = "确定要删除这个方案吗？"
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(title).assertExists()
    }

    @Test
    fun confirmDialog_shouldDisplayMessage() {
        val message = "确定要删除这个方案吗？"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    ConfirmDialog(
                        show = true,
                        onDismiss = {},
                        onConfirm = {},
                        title = "确认删除",
                        message = message
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(message).assertExists()
    }

    @Test
    fun confirmDialog_shouldDisplayButtons() {
        val confirmText = "删除"
        val dismissText = "取消"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    ConfirmDialog(
                        show = true,
                        onDismiss = {},
                        onConfirm = {},
                        title = "确认删除",
                        message = "确定要删除这个方案吗？",
                        confirmText = confirmText,
                        dismissText = dismissText
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(confirmText).assertExists()
        composeTestRule.onNodeWithText(dismissText).assertExists()
    }

    @Test
    fun smartDialog_shouldHandleSuccessType() {
        val title = "操作成功"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartDialog(
                        show = true,
                        onDismiss = {},
                        title = title,
                        type = DialogType.SUCCESS
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(title).assertExists()
    }

    @Test
    fun smartDialog_shouldHandleErrorType() {
        val title = "出错了"

        composeTestRule.setContent {
            MaterialTheme {
                Surface {
                    SmartDialog(
                        show = true,
                        onDismiss = {},
                        title = title,
                        type = DialogType.ERROR
                    )
                }
            }
        }

        composeTestRule.onNodeWithText(title).assertExists()
    }
}
