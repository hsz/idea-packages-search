package mobi.hsz.idea.packagessearch.actions

import com.intellij.icons.AllIcons
import com.intellij.ide.actions.SearchEverywhereAction.SEARCH_FIELD_COLUMNS
import com.intellij.ide.ui.UISettings
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextBorder
import com.intellij.ide.ui.laf.darcula.ui.DarculaTextFieldUI
import com.intellij.ide.ui.laf.intellij.MacIntelliJTextBorder
import com.intellij.ide.ui.laf.intellij.MacIntelliJTextFieldUI
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.IdeFrameImpl
import com.intellij.ui.*
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import java.awt.Font
import java.awt.Point
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JPanel

class PackagesSearchAction : AnAction() {
    private lateinit var balloon: JBPopup
    private lateinit var myPopupField: MySearchTextField

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project === null) {
            return
        }

        myPopupField = MySearchTextField()
        val editor = myPopupField.getTextEditor()
        editor.setColumns(SEARCH_FIELD_COLUMNS)
        val panel = JPanel(BorderLayout())
        val title = JLabel(" Search Everywhere:       ")
        val topPanel = NonOpaquePanel(BorderLayout())
        title.foreground = JBColor(Gray._240, Gray._200)
        if (SystemInfo.isMac) {
            title.font = title.font.deriveFont(Font.BOLD, title.font.size - 1f)
        } else {
            title.font = title.font.deriveFont(Font.BOLD)
        }
        topPanel.add(title, BorderLayout.WEST)
        val controls = JPanel(BorderLayout())
        controls.isOpaque = false
        val settings = JLabel(AllIcons.General.SearchEverywhereGear)
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
//                showSettings()
                return true
            }
        }.installOn(settings)
        controls.add(settings, BorderLayout.EAST)
        topPanel.add(controls, BorderLayout.EAST)
        panel.add(myPopupField, BorderLayout.CENTER)
        panel.add(topPanel, BorderLayout.NORTH)
        panel.border = IdeBorderFactory.createEmptyBorder(3, 5, 4, 5)

        val builder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, editor)
        balloon = builder
                .setCancelOnClickOutside(true)
                .setModalContext(false)
                .setRequestFocus(true)
                .setCancelCallback { true }
                .createPopup()
        balloon.getContent().setBorder(JBUI.Borders.empty())

        val window = WindowManager.getInstance().suggestParentWindow(project)

        val parent = UIUtil.findUltimateParent(window)
        val showPoint: RelativePoint
        if (parent != null) {
            var height = if (UISettings.instance.showMainToolbar) 135 else 115
            if (parent is IdeFrameImpl && parent.isInFullScreen) {
                height -= 20
            }
            showPoint = RelativePoint(parent, Point((parent.size.width - panel.preferredSize.width) / 2, height))
        } else {
            showPoint = JBPopupFactory.getInstance().guessBestPopupLocation(e.dataContext)
        }
        balloon.show(showPoint)
        val focusManager = IdeFocusManager.getInstance(project)
        focusManager.requestFocus(editor, true)
    }

    private class MySearchTextField : SearchTextField(false), DataProvider, Disposable {
        init {
            val editor = textEditor
            editor.isOpaque = false
            if (SystemInfo.isMac && UIUtil.isUnderIntelliJLaF()) {
                editor.ui = MacIntelliJTextFieldUI.createUI(editor) as MacIntelliJTextFieldUI
                editor.border = MacIntelliJTextBorder()
            } else {
                editor.ui = DarculaTextFieldUI.createUI(editor) as DarculaTextFieldUI
                editor.border = DarculaTextBorder()
            }

            editor.putClientProperty("JTextField.Search.noBorderRing", java.lang.Boolean.TRUE)
            if (UIUtil.isUnderDarcula()) {
                editor.background = Gray._45
                editor.foreground = Gray._240
            }
        }

        override fun isSearchControlUISupported(): Boolean {
            return true
        }

        override fun hasIconsOutsideOfTextField(): Boolean {
            return false
        }

        override fun showPopup() {}

        override fun getData(@NonNls dataId: String): Any? {
            return if (PlatformDataKeys.PREDEFINED_TEXT.`is`(dataId)) {
                textEditor.text
            } else null
        }

        override fun dispose() {}
    }
}