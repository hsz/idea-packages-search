package mobi.hsz.idea.packagessearch.actions

import com.intellij.find.FindBundle
import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Comparing
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.IdeFrameImpl
import com.intellij.ui.ClickListener
import com.intellij.ui.CollectionListModel
import com.intellij.ui.Gray
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import mobi.hsz.idea.packagessearch.components.PackagesSearchSettings
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.ui.PackagesSearchList
import mobi.hsz.idea.packagessearch.ui.PackagesSearchTextField
import mobi.hsz.idea.packagessearch.utils.ApiService
import mobi.hsz.idea.packagessearch.utils.Constants
import mobi.hsz.idea.packagessearch.utils.Constants.Companion.GRADIENT
import mobi.hsz.idea.packagessearch.utils.RegistryContext
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit
import javax.swing.JLabel
import javax.swing.KeyStroke
import kotlin.coroutines.CoroutineContext

class PackagesSearchAction : AnAction(), CoroutineScope, Disposable, DumbAware {
    private lateinit var packagesSearch: PackagesSearchTextField
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

    private val searchObservable: Subject<String> = PublishSubject.create()
    private val dataObservable: Subject<List<Package>> = PublishSubject.create()
    private val stateObservable: Subject<Pair<Boolean, Boolean>> = PublishSubject.create()

    private val disposable = CompositeDisposable()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project === null) {
            return
        }

        settings = PackagesSearchSettings.getInstance(project)
        focusManager = IdeFocusManager.getInstance(project)

        packagesSearch = PackagesSearchTextField(
            onTextChange = searchObservable::onNext,
            onKeyUp = { list.selectedIndex = list.selectedIndex - 1.coerceAtLeast(0) },
            onKeyDown = { list.selectedIndex = list.selectedIndex + 1.coerceAtMost(list.itemsCount - 1) },
            onKeyTab = { println("TAB!") }, // TODO implement -> show package details
            onKeyEnter = { println("ENTER!") } // TODO implement -> install package
        )

        currentRegistryLabel = JLabel(settings.state.registry.toString()).apply {
            border = JBEmptyBorder(0, 10, 0, 10)
            foreground = JBColor(Gray._240, Gray._200)
        }

        list = PackagesSearchList(
            model = listModel,
            onFocus = this::requestSearchFocus
        )
        hint = JBLabel().apply {
            isVisible = false
            border = IdeBorderFactory.createEmptyBorder(3, 3, 0, 3)
            fontColor = UIUtil.FontColor.BRIGHTER
            font = JBUI.Fonts.smallFont()
            text = PackagesSearchBundle.message("ui.hint")
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

        registryFilterButton = ActionButton(
            registryFilterPopupAction,
            registryFilterPopupAction.templatePresentation,
            ActionPlaces.UNKNOWN,
            ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE
        ).apply {
            isOpaque = false
        }

        // Header Panel: [Popup title, Current registry], [Registry filter, Settings]
        val header = NonOpaquePanel(BorderLayout()).apply {
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
                GRADIENT.apply {
                    (g as Graphics2D).paint = GradientPaint(0f, 0f, startColor, 0f, height.toFloat(), endColor)
                }
                g.fillRect(0, 0, width, height)
            }
        }.apply {
            border = IdeBorderFactory.createEmptyBorder(3, 5, 4, 5)

            add(header, BorderLayout.NORTH)
            add(packagesSearch, BorderLayout.CENTER)
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

        val builder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, packagesSearch.textEditor)
        val popup = builder
            .setCancelOnClickOutside(true)
            .setModalContext(false)
            .setRequestFocus(true)
            .setCancelCallback { true }
            .createPopup().apply {
                content.border = JBUI.Borders.empty()
                show(showPoint)
                Disposer.register(this, this@PackagesSearchAction)
            }

        baseSize = panel.size
        registryFilterPopupAction.registerCustomShortcutSet(CustomShortcutSet.fromString("alt P"), panel, popup)


        searchObservable.map(StringUtil::isNotEmpty).subscribe {
            job.cancelChildren()
            listModel.removeAll()
            when {
                it -> stateObservable.onNext(Pair(false, true))
                else -> stateObservable.onNext(Pair(true, false))
            }
        }.addTo(disposable)

        searchObservable.debounce(Constants.SEARCH_DELAY, TimeUnit.MILLISECONDS).filter(StringUtil::isNotEmpty)
            .subscribe {
                launch(coroutineContext) {
                    val (result) = ApiService.search(settings.state.registry, it, coroutineContext)
                    dataObservable.onNext(result!!.items)
                }
            }.addTo(disposable)

        dataObservable.subscribe {
            listModel.add(it)
            stateObservable.onNext(Pair(false, false))
        }.addTo(disposable)

        stateObservable.subscribe { (initial, loading) ->
            hint.isVisible = !initial

            list.apply {
                isVisible = !initial
                selectedIndex = 0
                setPaintBusy(!initial && loading)
                setEmptyText(
                    when {
                        loading -> PackagesSearchBundle.message("ui.list.searching")
                        else -> PackagesSearchBundle.message("ui.list.empty")
                    }
                )
            }

            val listHeight = when {
                initial -> 0
                else -> list.preferredSize.height + hint.preferredSize.height
            }
            popup.size = Dimension(baseSize.width, baseSize.height + listHeight)
        }
    }

    private fun requestSearchFocus() {
        focusManager.requestFocus(packagesSearch.textEditor, true)
    }

    override fun dispose() {
        job.cancelChildren()
        Disposer.dispose(packagesSearch)
        disposable.clear()
    }

    private fun registryChanged() {
        currentRegistryLabel.text = settings.state.registry.toString()
        requestSearchFocus()
    }

    private fun openSettings() = println("openSettings")

    inner class RegistryFilterPopupAction :
        AnAction(FindBundle.message("find.popup.show.filter.popup"), "Description", AllIcons.General.MoreTabs) {
        private val switchContextGroup: DefaultActionGroup

        init {
            shortcutSet = CustomShortcutSet(
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_F, when {
                        SystemInfo.isMac -> InputEvent.CTRL_DOWN_MASK or InputEvent.ALT_DOWN_MASK
                        else -> InputEvent.ALT_DOWN_MASK
                    }
                )
            )

            switchContextGroup = DefaultActionGroup().apply {
                isPopup = true
                RegistryContext.values().iterator().forEach {
                    add(SwitchRegistryToggleAction(it))
                }
            }
        }

        override fun actionPerformed(e: AnActionEvent) {
            if (PlatformDataKeys.CONTEXT_COMPONENT.getData(e.dataContext) == null) return

            val listPopup = JBPopupFactory.getInstance()
                .createActionGroupPopup(null, switchContextGroup, e.dataContext, false, null, 10)
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