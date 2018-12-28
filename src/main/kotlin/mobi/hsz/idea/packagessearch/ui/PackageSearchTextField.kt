package mobi.hsz.idea.packagessearch.ui

import com.intellij.ide.actions.SearchEverywhereAction
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextBorder
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextFieldUI
import com.intellij.ide.ui.laf.intellij.MacIntelliJTextBorder
import com.intellij.ide.ui.laf.intellij.MacIntelliJTextFieldUI
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.Gray
import com.intellij.ui.SearchTextField
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.NonNls

class PackageSearchTextField : SearchTextField(false), DataProvider, Disposable {
    init {
        isOpaque = false

        textEditor.apply {
            columns = SearchEverywhereAction.SEARCH_FIELD_COLUMNS
            isOpaque = false
            putClientProperty("JTextField.Search.noBorderRing", java.lang.Boolean.TRUE)

            border = when {
                SystemInfo.isMac && UIUtil.isUnderIntelliJLaF() -> {
                    setUI(MacIntelliJTextFieldUI.createUI(this) as MacIntelliJTextFieldUI)
                    MacIntelliJTextBorder()
                }
                else -> {
                    setUI(DarculaTextFieldUI.createUI(this) as DarculaTextFieldUI)
                    DarculaTextBorder()
                }
            }

            if (UIUtil.isUnderDarcula()) {
                background = Gray._45
                foreground = Gray._240
            }
        }
    }

    override fun isSearchControlUISupported() = true

    override fun hasIconsOutsideOfTextField() = false

    override fun showPopup() = Unit

    override fun getData(@NonNls dataId: String) = when {
        PlatformDataKeys.PREDEFINED_TEXT.`is`(dataId) -> textEditor.text
        else -> null
    }

    override fun dispose() = Unit
}
