package mobi.hsz.idea.packagessearch.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.util.ui.JBUI
import mobi.hsz.idea.packagessearch.components.PackagesSearchSettings
import mobi.hsz.idea.packagessearch.ui.PackagesSearchPanel
import mobi.hsz.idea.packagessearch.utils.RxBus
import mobi.hsz.idea.packagessearch.utils.events.RegistryChangedEvent

class PackagesSearchAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project === null) {
            return
        }

        val settings = PackagesSearchSettings.getInstance(project)
        val panel = PackagesSearchPanel(project)

        val showPoint = panel.getShowPoint(project, e.dataContext)
        val builder = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, panel.getPreferableFocusComponent())
        val popup = builder
            .setCancelOnClickOutside(true)
            .setModalContext(false)
            .setRequestFocus(true)
            .setCancelCallback { true }
            .createPopup().apply {
                content.border = JBUI.Borders.empty()
                show(showPoint)
            }

        Disposer.register(popup, panel)

        RxBus.publish(RegistryChangedEvent(settings.state.registry))
    }
}
