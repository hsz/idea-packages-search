package mobi.hsz.idea.packagessearch.ui.components

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.ui.WindowMoveListener
import com.intellij.ui.components.JBList
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.components.BorderLayoutPanel
import org.jdesktop.swingx.renderer.DefaultListRenderer
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.ListCellRenderer




class PackagesSearchUI(project: Project) : BorderLayoutPanel(), Disposable {
    init {
        withBackground(UIUtil.getEditorPaneBackground())

//        myResultsList = createList()

        val topLeftPanel = createTopLeftPanel()
        val settingsPanel = createSettingsPanel()
//        val mySearchField = SearchField()
//        suggestionsPanel = createSuggestionsPanel()

//        myResultsList.setFocusable(false)
//        myResultsList.setCellRenderer(createCellRenderer())

//        installScrollingActions()

        val topPanel = JPanel(BorderLayout())
        topPanel.isOpaque = false
        topPanel.add(topLeftPanel, BorderLayout.WEST)
        topPanel.add(settingsPanel, BorderLayout.EAST)
//        topPanel.add(mySearchField, BorderLayout.SOUTH)

        val moveListener = WindowMoveListener(this)
        topPanel.addMouseListener(moveListener)
        topPanel.addMouseMotionListener(moveListener)

        addToTop(topPanel)
//        addToCenter(suggestionsPanel)
    }

    fun getInitialHint(): String {
        return "zxc"
    }

    override fun dispose() {
    }

    fun createTopLeftPanel(): JPanel {
        val contributorsPanel = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))

        return contributorsPanel
    }

    fun createList(): JBList<Any> {

        return JBList<Any>()
    }

    fun createSettingsPanel(): JPanel {
        val res = JPanel()

        return res
    }

    fun createCellRenderer(): ListCellRenderer<Any> {
        return DefaultListRenderer()
    }

    fun getSearchField(): JComponent? {
        return null
    }
}