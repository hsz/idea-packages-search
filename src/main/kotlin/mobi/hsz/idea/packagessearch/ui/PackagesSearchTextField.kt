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
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.Gray
import com.intellij.ui.SearchTextField
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.NonNls
import java.awt.KeyboardFocusManager
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.util.Collections
import javax.swing.event.DocumentEvent

class PackagesSearchTextField(
    var onTextChange: (text: String) -> Unit,
    var onKeyUp: () -> Unit,
    var onKeyDown: () -> Unit,
    var onKeyTab: () -> Unit,
    var onKeyEnter: () -> Unit
) : SearchTextField(false), DataProvider, Disposable {
    init {
        isOpaque = false

        textEditor.apply {
            setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet())

            columns = SearchEverywhereAction.SEARCH_FIELD_COLUMNS
            isOpaque = false
            putClientProperty("JTextField.Search.noBorderRing", java.lang.Boolean.TRUE)

            border = when {
                SystemInfo.isMac && UIUtil.isUnderIntelliJLaF() -> {
                    ui = MacIntelliJTextFieldUI.createUI(this) as MacIntelliJTextFieldUI
                    MacIntelliJTextBorder()
                }
                else -> {
                    ui = DarculaTextFieldUI.createUI(this) as DarculaTextFieldUI
                    DarculaTextBorder()
                }
            }

            if (UIUtil.isUnderDarcula()) {
                background = Gray._45
                foreground = Gray._240
            }

            document.addDocumentListener(object : DocumentAdapter() {
                override fun textChanged(e: DocumentEvent) {
                    when {
                        hasFocus() -> onTextChange(text)
                    }
                }
            })

            addKeyListener(object : KeyAdapter() {
                override fun keyPressed(e: KeyEvent?) {
                    when (e?.keyCode) {
                        KeyEvent.VK_UP -> onKeyUp()
                        KeyEvent.VK_DOWN -> onKeyDown()
                        KeyEvent.VK_TAB -> onKeyTab()
                        KeyEvent.VK_ENTER -> onKeyEnter()
                    }
                }
            })
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
