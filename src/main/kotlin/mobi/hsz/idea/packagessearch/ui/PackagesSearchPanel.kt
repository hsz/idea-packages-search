package mobi.hsz.idea.packagessearch.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
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
import mobi.hsz.idea.packagessearch.actions.RegistryFilterPopupAction
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.utils.ApiService
import mobi.hsz.idea.packagessearch.utils.Constants
import mobi.hsz.idea.packagessearch.utils.RegistryContext
import mobi.hsz.idea.packagessearch.utils.RxBus
import mobi.hsz.idea.packagessearch.utils.events.RegistryChangedEvent
import mobi.hsz.idea.packagessearch.utils.events.RequestSearchFocusEvent
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.event.MouseEvent
import java.util.concurrent.TimeUnit
import javax.swing.JLabel
import kotlin.coroutines.CoroutineContext

class PackagesSearchPanel(
    project: Project,
    dataContext: DataContext
) : JBPanel<JBPanel<*>>(BorderLayout()), CoroutineScope, Disposable {
    /** Enhanced ListModel containing current fetched [Package] entities. */
    private val listModel = CollectionListModel<Package>()

    /** List component displayed in the center of popup. */
    private val list = PackagesSearchList(listModel)

    private val disposable = CompositeDisposable()

    private val job = Job()
    private val searchObservable: Subject<String> = PublishSubject.create()
    private val dataObservable: Subject<List<Package>> = PublishSubject.create()
    private val stateObservable: Subject<Pair<Boolean, Boolean>> = PublishSubject.create()

    private var initialSize: Dimension

    private lateinit var registry: RegistryContext

    // Title label with `ui.title` text
    private val title = JLabel(PackagesSearchBundle.message("ui.title")).apply {
        foreground = JBColor(Gray._240, Gray._200)
        font = font.deriveFont(Font.BOLD)
    }

    // Current registry label - updated on registry change
    private val currentRegistryLabel = JLabel().apply {
        border = JBEmptyBorder(0, 10, 0, 10)
        foreground = JBColor(Gray._240, Gray._200)

        RxBus.listen(RegistryChangedEvent::class.java).subscribe {
            it.context.apply {
                text = toString()
                registry = this
            }
        }.addTo(disposable)
    }

    private val registryFilterPopupAction = RegistryFilterPopupAction()

    private val registryFilterButton = ActionButton(
        registryFilterPopupAction,
        registryFilterPopupAction.templatePresentation,
        ActionPlaces.UNKNOWN,
        ActionToolbar.DEFAULT_MINIMUM_BUTTON_SIZE
    ).apply {
        isOpaque = false
    }

    private val settingsButton = JLabel(AllIcons.General.SearchEverywhereGear).apply {
        object : ClickListener() {
            override fun onClick(event: MouseEvent, clickCount: Int): Boolean {
                openSettings()
                return true
            }
        }.installOn(this)
    }

    private val hint = JBLabel().apply {
        isVisible = false
        border = IdeBorderFactory.createEmptyBorder(3, 3, 0, 3)
        fontColor = UIUtil.FontColor.BRIGHTER
        font = JBUI.Fonts.smallFont()
        text = PackagesSearchBundle.message("ui.hint")
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    init {
        border = IdeBorderFactory.createEmptyBorder(3, 5, 4, 5)
        registryFilterPopupAction.apply {
            registerCustomShortcutSet(
                CustomShortcutSet.fromString("alt P"),
                this@PackagesSearchPanel
            ) // TODO remove hardcoded value
            positionReferenceComponent = registryFilterButton
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

        val packagesSearch = PackagesSearchTextField(
            onTextChange = searchObservable::onNext,
            onKeyUp = { list.selectedIndex = list.selectedIndex - 1.coerceAtLeast(0) },
            onKeyDown = { list.selectedIndex = list.selectedIndex + 1.coerceAtMost(list.itemsCount - 1) },
            onKeyTab = { println("TAB!") }, // TODO implement -> show package details
            onKeyEnter = { println("ENTER!") } // TODO implement -> install package
        )

        add(header, BorderLayout.NORTH)
        add(packagesSearch, BorderLayout.CENTER)
        add(NonOpaquePanel(BorderLayout()).apply {
            add(list, BorderLayout.NORTH)
            add(hint, BorderLayout.SOUTH)
        }, BorderLayout.SOUTH)

        val window = WindowManager.getInstance().suggestParentWindow(project)
        val parent = UIUtil.findUltimateParent(window)
        val showPoint: RelativePoint = when {
            parent != null -> {
                var height = if (UISettings.instance.showMainToolbar) 135 else 115
                if (parent is IdeFrameImpl && parent.isInFullScreen) {
                    height -= 20
                }
                RelativePoint(parent, Point((parent.size.width - preferredSize.width) / 2, height))
            }
            else -> JBPopupFactory.getInstance().guessBestPopupLocation(dataContext)
        }

        val builder = JBPopupFactory.getInstance().createComponentPopupBuilder(this, packagesSearch.textEditor)
        val popup = builder
            .setCancelOnClickOutside(true)
            .setModalContext(false)
            .setRequestFocus(true)
            .setCancelCallback { true }
            .createPopup().apply {
                content.border = JBUI.Borders.empty()
                show(showPoint)
            }

        initialSize = size

        searchObservable.map(StringUtil::isNotEmpty).subscribe {
            job.cancelChildren()
            listModel.removeAll()
            when {
                it -> stateObservable.onNext(false to true)
                else -> stateObservable.onNext(true to false)
            }
        }

        searchObservable.debounce(Constants.SEARCH_DELAY, TimeUnit.MILLISECONDS).filter(StringUtil::isNotEmpty)
            .subscribe {
                launch(coroutineContext) {
                    val result = ApiService.search(registry, it, coroutineContext)
                    dataObservable.onNext(result.items)
                }
            }

        dataObservable.subscribe {
            listModel.add(it)
            stateObservable.onNext(false to false)
        }

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
            popup.size = Dimension(initialSize.width, initialSize.height + listHeight)
        }

        RxBus.listen(RequestSearchFocusEvent::class.java).subscribe {
            IdeFocusManager.getInstance(project).requestFocus(packagesSearch.textEditor, true)
        }

        Disposer.register(popup, this)
    }

    override fun paintComponent(g: Graphics) {
        Constants.GRADIENT.apply {
            (g as Graphics2D).paint = GradientPaint(0f, 0f, startColor, 0f, height.toFloat(), endColor)
        }
        g.fillRect(0, 0, width, height)
    }

    private fun openSettings() = println("openSettings")

    override fun dispose() {
        registryFilterPopupAction.dispose()
        disposable.clear()
    }
}