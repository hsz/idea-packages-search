package mobi.hsz.idea.packagessearch.actions

import com.intellij.find.FindModel
import com.intellij.find.impl.FindDialog
import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.IdeFrameImpl
import com.intellij.ui.*
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBList
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import mobi.hsz.idea.packagessearch.components.PackagesSearchSettings
import mobi.hsz.idea.packagessearch.ui.PackageSearchTextField
import java.awt.*
import java.awt.event.MouseEvent
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel

class PackagesSearchAction : AnAction(), Disposable {
    private lateinit var balloon: JBPopup
    private lateinit var packageSearch: PackageSearchTextField

    override fun actionPerformed(e: AnActionEvent) {
        if (::balloon.isInitialized && balloon.isVisible && !balloon.isDisposed) {
            return
        }
        if (::balloon.isInitialized) {
            Disposer.dispose(balloon)
        }

        val project = e.project
        if (project === null) {
            return
        }

        val settings = PackagesSearchSettings.getInstance(project)
        val registry = settings.state.registry
        packageSearch = PackageSearchTextField()

        val title = JLabel(PackagesSearchBundle.message("ui.title")).apply {
            foreground = JBColor(Gray._240, Gray._200)
            font = when {
                SystemInfo.isMac -> font.deriveFont(Font.BOLD, font.size - 1f)
                else -> font.deriveFont(Font.BOLD)
            }
        }

        val currentRegistry = JLabel(registry.toString()).apply {
            border = JBEmptyBorder(0, 5, 0, 5)
            foreground = JBColor(Gray._240, Gray._200)
        }

        val settingsButton = JLabel(AllIcons.General.SearchEverywhereGear).apply {
            object : ClickListener() {
                override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                    openSettings()
                    return true
                }
            }.installOn(this)
        }

        val myShowFilterPopupAction = RegistryFilterPopupAction()
        val registryFilterButton = object : ActionButton(myShowFilterPopupAction, myShowFilterPopupAction.templatePresentation, ActionPlaces.UNKNOWN, ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE) {
            init {
                myShowFilterPopupAction.filterContextButton = this
                isOpaque = false
            }
            override fun getPopState(): Int {
                val state = super.getPopState()
                if (state != ActionButtonComponent.NORMAL) return state
                return if ("foo" == FindDialog.getPresentableName(FindModel.SearchContext.ANY))
                    ActionButtonComponent.NORMAL
                else
                    ActionButtonComponent.PUSHED
            }
        }

        val titlePanel = NonOpaquePanel(BorderLayout()).apply {
            add(title, BorderLayout.WEST)
            add(currentRegistry, BorderLayout.EAST)
        }
        val controls = NonOpaquePanel(BorderLayout()).apply {
            add(registryFilterButton, BorderLayout.WEST)
            add(settingsButton, BorderLayout.EAST)
        }

        val topPanel = NonOpaquePanel(BorderLayout()).apply {
            add(titlePanel, BorderLayout.WEST)
            add(controls, BorderLayout.EAST)
        }

        val resultsPanel = NonOpaquePanel(BorderLayout()).apply {
            val list = JBList<String>("webpack", "webpack-dev-server", "uglifyjs-webpack-plugin").apply {
                cellRenderer = object: ColoredListCellRenderer<String>() {
                    override fun customizeCellRenderer(list: JList<out String>, value: String?, index: Int, selected: Boolean, hasFocus: Boolean) {
                        append(value!!)
                    }
                }
            }
            add(list)
        }

        val panel = object : JPanel(BorderLayout()) {
            override fun paintComponent(g: Graphics) {
                val gradient = getGradientColors()
                (g as Graphics2D).paint = GradientPaint(0f, 0f, gradient.startColor, 0f, height.toFloat(), gradient.endColor)
                g.fillRect(0, 0, width, height)
            }
        }.apply {
            border = IdeBorderFactory.createEmptyBorder(3, 5, 4, 5)
            add(packageSearch, BorderLayout.CENTER)
            add(topPanel, BorderLayout.NORTH)
            add(resultsPanel, BorderLayout.SOUTH)
        }


        val window = WindowManager.getInstance().suggestParentWindow(project)
        val parent = UIUtil.findUltimateParent(window)
        val showPoint: RelativePoint = when {
            parent != null -> {
                var height = if (UISettings.instance.showMainToolbar) 135 else 115
                if (parent is IdeFrameImpl && parent.isInFullScreen) {
                    height -= 20
                }
                RelativePoint(parent, Point((parent.size.width - panel.preferredSize.width) / 2, height))
            }
            else -> JBPopupFactory.getInstance().guessBestPopupLocation(e.dataContext)
        }

        val builder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, packageSearch.textEditor)
        balloon = builder
                .setCancelOnClickOutside(true)
                .setModalContext(false)
                .setRequestFocus(true)
                .setCancelCallback { true }
                .createPopup().apply {
                    content.border = JBUI.Borders.empty()
                    show(showPoint)
                }

        IdeFocusManager.getInstance(project).requestFocus(packageSearch.textEditor, true)
    }

    override fun dispose() = Disposer.dispose(packageSearch)

    private fun openSettings() = println("openSettings")

    private fun getGradientColors() = Gradient(
            JBColor(0x65F065, 0x455C3F),
            JBColor(0x3CCC2F, 0x365735))

}