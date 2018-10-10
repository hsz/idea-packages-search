package mobi.hsz.idea.packagessearch.actions

import com.intellij.find.FindBundle
import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.application.ApplicationManager
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
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import kotlinx.coroutines.experimental.*
import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import mobi.hsz.idea.packagessearch.components.PackagesSearchSettings
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.ui.PackageSearchTextField
import mobi.hsz.idea.packagessearch.utils.ApiService
import mobi.hsz.idea.packagessearch.utils.Constants.Companion.GRADIENT
import mobi.hsz.idea.packagessearch.utils.Constants.Companion.SEARCH_DELAY
import mobi.hsz.idea.packagessearch.utils.RegistryContext
import java.awt.*
import java.awt.event.*
import javax.swing.JLabel
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import kotlin.coroutines.experimental.CoroutineContext

class PackagesSearchAction : AnAction(), Disposable, CoroutineScope {
    private lateinit var popup: JBPopup
    private lateinit var header: NonOpaquePanel
    private lateinit var packageSearch: PackageSearchTextField
    private lateinit var registryFilterButton: ActionButton
    private lateinit var settings: PackagesSearchSettings
    private lateinit var currentRegistryLabel: JLabel
    private lateinit var focusManager: IdeFocusManager
    private lateinit var list: JBList<Package>
    private lateinit var hint: JBLabel
    private lateinit var panel: JBPanel<JBPanel<*>>
    private lateinit var baseSize: Dimension
    private val job = Job()
    private var listModel = CollectionListModel<Package>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

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
        packageSearch = PackageSearchTextField().apply {
            textEditor.apply {
                document.addDocumentListener(object : DocumentAdapter() {
                    override fun textChanged(e: DocumentEvent) {
                        job.cancelChildren()
                        if (!hasFocus()) {
                            return
                        }

                        if (text.isEmpty()) {
                            rebuildList(visible = false)
                        } else {
                            rebuildList(loading = true)
                            launch(coroutineContext) {
                                delay(SEARCH_DELAY)
                                val (result) = ApiService.search(settings.state.registry, text)
                                SwingUtilities.invokeLater {
                                    rebuildList(data = result?.items, loading = false)
                                }
                            }
                        }
                    }
                })

                addKeyListener(object : KeyAdapter() {
                    override fun keyPressed(e: KeyEvent?) {
                        when (e?.keyCode) {
                            KeyEvent.VK_UP -> list.selectedIndex = Math.max(list.selectedIndex - 1, 0)
                            KeyEvent.VK_DOWN -> list.selectedIndex = Math.min(list.selectedIndex + 1, list.itemsCount - 1)
                            KeyEvent.VK_TAB -> println("TAB!") // TODO implement -> show package details
                            KeyEvent.VK_ENTER -> println("ENTER!") // TODO implement -> install package
                        }
                    }
                })
            }
        }

        focusManager = IdeFocusManager.getInstance(project)
        focusManager.requestFocus(packageSearch.textEditor, true)

        currentRegistryLabel = JLabel(settings.state.registry.toString()).apply {
            border = JBEmptyBorder(0, 10, 0, 10)
            foreground = JBColor(Gray._240, Gray._200)
        }

        list = JBList<Package>(listModel.apply { removeAll() }).apply {
            isVisible = false
            installCellRenderer<Package> { pkg -> PackageCell(pkg) }
            addFocusListener(object : FocusListener {
                override fun focusLost(e: FocusEvent?) {
                }

                override fun focusGained(e: FocusEvent?) {
                    focusManager.requestFocus(packageSearch.textEditor, true)
                }
            })
            addMouseListener(object : MouseListener {
                override fun mouseReleased(e: MouseEvent?) {
                }

                override fun mouseEntered(e: MouseEvent?) {
                }

                override fun mouseExited(e: MouseEvent?) {
                }

                override fun mousePressed(e: MouseEvent?) {
                }

                override fun mouseClicked(e: MouseEvent?) {
                }
            })
        }
        hint = JBLabel().apply {
            isVisible = false
            border = IdeBorderFactory.createEmptyBorder(3, 3, 0, 3)
            fontColor = UIUtil.FontColor.BRIGHTER
            font = JBUI.Fonts.smallFont()
            text = "[Tab] show package details   [Enter] install   [Alt+Enter] open in browser" // TODO use enhance, move to messages
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
            add(NonOpaquePanel(BorderLayout()).apply {
                add(list, BorderLayout.NORTH)
                add(hint, BorderLayout.SOUTH)
            }, BorderLayout.SOUTH)
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

        baseSize = panel.size
        registryFilterPopupAction.registerCustomShortcutSet(CustomShortcutSet.fromString("alt P"), panel, popup)
    }

    override fun dispose() {
        Disposer.dispose(packageSearch)
        job.cancel()
    }

    private fun rebuildList(data: List<Package>? = null, visible: Boolean = true, loading: Boolean = false) {
        ApplicationManager.getApplication().assertIsDispatchThread()
        if (data === null) {
            listModel.removeAll()
        } else {
            listModel.replaceAll(data)
        }

        list.apply {
            isVisible = visible
            selectedIndex = 0
            setPaintBusy(loading)
            setEmptyText(when {
                loading -> PackagesSearchBundle.message("ui.list.searching")
                else -> PackagesSearchBundle.message("ui.list.empty")
            })
        }
        hint.isVisible = visible

        val listHeight = when (visible) {
            true -> list.preferredSize.height + hint.preferredSize.height
            false -> 0
        }
        popup.size = Dimension(baseSize.width, baseSize.height + listHeight)
    }

    private fun registryChanged() {
        currentRegistryLabel.text = settings.state.registry.toString()
        focusManager.requestFocus(packageSearch.textEditor, true)
    }

    private fun openSettings() = println("openSettings")

    private fun getGradientColors() = GRADIENT

    inner class PackageCell(pkg: Package) : NonOpaquePanel(BorderLayout()) {
        init {
//            border = JBEmptyBorder(3)
            border = JBUI.Borders.merge(
                    JBUI.Borders.empty(5),
                    JBUI.Borders.customLine(JBColor.GRAY.darker(), 1, 0, 0, 0),
                    true
            )

            add(JBLabel(pkg.name).apply {
                font = JBUI.Fonts.label().asBold()
            }, BorderLayout.WEST)

            add(JBLabel(pkg.version).apply {
                fontColor = UIUtil.FontColor.BRIGHTER
            }, BorderLayout.EAST)

            add(JBLabel(pkg.description).apply {
                border = JBEmptyBorder(3, 0, 0, 0)
                fontColor = UIUtil.FontColor.BRIGHTER
            }, BorderLayout.SOUTH)
        }
    }

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