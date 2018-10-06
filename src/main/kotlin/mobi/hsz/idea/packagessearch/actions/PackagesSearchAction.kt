package mobi.hsz.idea.packagessearch.actions

import com.intellij.find.FindBundle
import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.IdeFrameImpl
import com.intellij.ui.*
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import mobi.hsz.idea.packagessearch.components.PackagesSearchSettings
import mobi.hsz.idea.packagessearch.models.NpmPackage
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.ui.PackageSearchTextField
import mobi.hsz.idea.packagessearch.utils.ApiService
import mobi.hsz.idea.packagessearch.utils.RegistryContext
import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.KeyStroke
import javax.swing.event.DocumentEvent

class PackagesSearchAction : AnAction(), Disposable {
    private lateinit var popup: JBPopup
    private lateinit var header: NonOpaquePanel
    private lateinit var packageSearch: PackageSearchTextField
    private lateinit var registryFilterButton: ActionButton
    private lateinit var settings: PackagesSearchSettings
    private lateinit var currentRegistryLabel: JLabel
    private lateinit var focusManager: IdeFocusManager
    private lateinit var list: JBList<Package>
    private lateinit var panel: JBPanel<JBPanel<*>>
    private var baseHeight: Int = 0
    private var listModel = DefaultListModel<Package>()
    private var loading = false


    override fun actionPerformed(e: AnActionEvent) {
        if (::popup.isInitialized && popup.isVisible && !popup.isDisposed) {
            return
        }
        if (::popup.isInitialized) {
            Disposer.dispose(popup)
        }

        val project = e.project
        if (project === null) {
            return
        }

        settings = PackagesSearchSettings.getInstance(project)
        packageSearch = PackageSearchTextField()
        packageSearch.textEditor.apply {
            document.addDocumentListener(object : DocumentAdapter() {

                private var job: Job? = null

                override fun textChanged(e: DocumentEvent) {
                    job?.cancel()

                    if (text.isEmpty()) {
                        rebuildList(null, false)
                    } else {
                        loading = true
                        rebuildList(null)
                        job = launch {
                            delay(500)
                            ApiService.search(settings.state.registry, text) { (response, error) ->
                                println(response)
                                loading = false
                                rebuildList(response?.items)
                            }
                        }
                    }
                }
            })
        }
        focusManager = IdeFocusManager.getInstance(project)
        focusManager.requestFocus(packageSearch.textEditor, true)

        currentRegistryLabel = JLabel(settings.state.registry.toString()).apply {
            border = JBEmptyBorder(0, 5, 0, 5)
            foreground = JBColor(Gray._240, Gray._200)
        }

        list = JBList<Package>(listModel.apply { clear() }).apply {
            isVisible = false
            cellRenderer = object : ColoredListCellRenderer<Package>() {
                override fun customizeCellRenderer(list: JList<out Package>, value: Package?, index: Int, selected: Boolean, hasFocus: Boolean) {
                    append(value!!.name)
                }
            }
        }
        val registryFilterPopupAction = RegistryFilterPopupAction()
        val title = JLabel(PackagesSearchBundle.message("ui.title")).apply {
            foreground = JBColor(Gray._240, Gray._200)
            font = font.deriveFont(Font.BOLD)
        }
        val settingsButton = JLabel(AllIcons.General.SearchEverywhereGear).apply {
            object : ClickListener() {
                override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                    openSettings()
                    return true
                }
            }.installOn(this)
        }

        registryFilterButton = ActionButton(registryFilterPopupAction, registryFilterPopupAction.templatePresentation, ActionPlaces.UNKNOWN, ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE).apply {
            isOpaque = false
        }

        header = NonOpaquePanel(BorderLayout()).apply {
            add(NonOpaquePanel(BorderLayout()).apply {
                add(title, BorderLayout.WEST)
                add(currentRegistryLabel, BorderLayout.EAST)
            }, BorderLayout.WEST)
            add(NonOpaquePanel(BorderLayout()).apply {
                add(registryFilterButton, BorderLayout.WEST)
                add(settingsButton, BorderLayout.EAST)
            }, BorderLayout.EAST)
        }

        panel = object : JBPanel<JBPanel<*>>(BorderLayout()) {
            override fun paintComponent(g: Graphics) {
                getGradientColors().apply {
                    (g as Graphics2D).paint = GradientPaint(0f, 0f, startColor, 0f, height.toFloat(), endColor)
                }
                g.fillRect(0, 0, width, height)
            }
        }.apply {
            border = IdeBorderFactory.createEmptyBorder(3, 5, 4, 5)

            add(header, BorderLayout.NORTH)
            add(packageSearch, BorderLayout.CENTER)
            add(NonOpaquePanel(BorderLayout()).apply { add(list) }, BorderLayout.SOUTH)
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
        popup = builder
                .setCancelOnClickOutside(true)
                .setModalContext(false)
                .setRequestFocus(true)
                .setCancelCallback { true }
                .createPopup().apply {
                    content.border = JBUI.Borders.empty()
                    show(showPoint)
                }

        baseHeight = panel.size.height
        registryFilterPopupAction.registerCustomShortcutSet(CustomShortcutSet.fromString("alt P"), panel, popup)
    }

    override fun dispose() = Disposer.dispose(packageSearch)

    private fun rebuildList(data: List<NpmPackage>?) = rebuildList(data, true)

    private fun rebuildList(data: List<NpmPackage>?, visible: Boolean) {
        val newModel = DefaultListModel<Package>().apply {
            data?.forEach(this::addElement)
        }

        list.apply {
            model = newModel
            isVisible = visible
            setEmptyText(when {
                loading -> "Loading..."
                else -> "No packages found"
            })
            revalidate()
            repaint()
        }

        if (popup.isVisible) {
            val listHeight = when (list.isVisible) {
                true -> list.preferredSize.height
                false -> 0
            }

            popup.size = Dimension(popup.size.width, baseHeight + listHeight)
        }
    }

    private fun registryChanged() {
        currentRegistryLabel.text = settings.state.registry.toString()
        focusManager.requestFocus(packageSearch.textEditor, true)
    }

    private fun openSettings() = println("openSettings")

    private fun getGradientColors() = Gradient(
            JBColor(0x65F065, 0x455C3F),
            JBColor(0x3CCC2F, 0x365735))

    inner class RegistryFilterPopupAction : AnAction(FindBundle.message("find.popup.show.filter.popup"), "Description", AllIcons.General.MoreTabs) {
        private val switchContextGroup: DefaultActionGroup

        init {
            shortcutSet = CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_F, when {
                SystemInfo.isMac -> InputEvent.CTRL_DOWN_MASK or InputEvent.ALT_DOWN_MASK
                else -> InputEvent.ALT_DOWN_MASK
            }))

            switchContextGroup = DefaultActionGroup().apply {
                isPopup = true
                RegistryContext.values().iterator().forEach {
                    add(SwitchRegistryToggleAction(it))
                }
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            if (PlatformDataKeys.CONTEXT_COMPONENT.getData(e.dataContext) == null) return

            val listPopup = JBPopupFactory.getInstance().createActionGroupPopup(null, switchContextGroup, e.dataContext, false, null, 10)
            listPopup.showUnderneathOf(registryFilterButton)
        }
    }

    inner class SwitchRegistryToggleAction(private val context: RegistryContext) : ToggleAction(context.toString()) {
        override fun isSelected(e: AnActionEvent): Boolean {
            return Comparing.equal(settings.state.registry.toString(), templatePresentation.text)
        }

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            if (state) {
                settings.state.registry = context
                registryChanged()
            }
        }
    }
}