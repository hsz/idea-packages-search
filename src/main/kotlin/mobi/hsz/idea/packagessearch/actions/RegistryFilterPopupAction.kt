package mobi.hsz.idea.packagessearch.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CustomShortcutSet
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.SystemInfo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import mobi.hsz.idea.packagessearch.utils.RegistryContext
import mobi.hsz.idea.packagessearch.utils.RxBus
import mobi.hsz.idea.packagessearch.utils.events.RegistryChangedEvent
import mobi.hsz.idea.packagessearch.utils.events.RequestSearchFocusEvent
import java.awt.Component
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

class RegistryFilterPopupAction :
    AnAction(
        PackagesSearchBundle.message("ui.scope.title"),
        PackagesSearchBundle.message("ui.scope.description"),
        AllIcons.General.MoreTabs
    ), Disposable {

    private val switchContextGroup: DefaultActionGroup
    private val popupFactory = JBPopupFactory.getInstance()
    private val disposable = CompositeDisposable()
    private var currentRegistry: RegistryContext? = null
    var positionReferenceComponent: Component? = null

    init {
        shortcutSet = CustomShortcutSet(
            KeyStroke.getKeyStroke(
                KeyEvent.VK_F, when {
                    SystemInfo.isMac -> InputEvent.CTRL_DOWN_MASK or InputEvent.ALT_DOWN_MASK
                    else -> InputEvent.ALT_DOWN_MASK
                }
            )
        )

        RxBus.listen(RegistryChangedEvent::class.java).subscribe {
            currentRegistry = it.context
        }.addTo(disposable)

        switchContextGroup = DefaultActionGroup().apply {
            isPopup = true

            RegistryContext.values().iterator().forEach {
                add(object : ToggleAction(it.toString()) {
                    override fun isSelected(e: AnActionEvent?) = it == currentRegistry

                    override fun setSelected(e: AnActionEvent?, state: Boolean) {
                        if (state) {
                            RxBus.publish(
                                RegistryChangedEvent(it),
                                RequestSearchFocusEvent()
                            )
                        }
                    }
                })
            }
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (PlatformDataKeys.CONTEXT_COMPONENT.getData(e.dataContext) == null || positionReferenceComponent == null) {
            return
        }

        popupFactory.createActionGroupPopup(null, switchContextGroup, e.dataContext, false, null, 10)
            .showUnderneathOf(this.positionReferenceComponent!!)
    }

    override fun dispose() {
        disposable.clear()
    }
}
