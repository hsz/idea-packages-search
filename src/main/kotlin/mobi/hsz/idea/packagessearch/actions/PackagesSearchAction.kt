package mobi.hsz.idea.packagessearch.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import mobi.hsz.idea.packagessearch.components.PackagesSearchSettings
import mobi.hsz.idea.packagessearch.ui.PackagesSearchPanel
import mobi.hsz.idea.packagessearch.utils.RxBus
import mobi.hsz.idea.packagessearch.utils.events.RegistryChangedEvent

class PackagesSearchAction : AnAction(), DumbAware {
    private lateinit var settings: PackagesSearchSettings
    private lateinit var panel: PackagesSearchPanel

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project === null) {
            return
        }

        settings = PackagesSearchSettings.getInstance(project)
        panel = PackagesSearchPanel(project, e.dataContext)

        RxBus.publish(RegistryChangedEvent(settings.state.registry))
    }
}
