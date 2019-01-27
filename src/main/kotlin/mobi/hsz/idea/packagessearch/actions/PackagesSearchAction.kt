package mobi.hsz.idea.packagessearch.actions

import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.Disposer
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ui.JBUI
import mobi.hsz.idea.packagessearch.components.PackagesSearchSettings
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.ui.PackageDetails
import mobi.hsz.idea.packagessearch.ui.PackagesSearchPanel
import mobi.hsz.idea.packagessearch.utils.RxBus
import mobi.hsz.idea.packagessearch.utils.events.RegistryChangedEvent
import java.awt.Point

class PackagesSearchAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project === null) {
            return
        }

        val settings = PackagesSearchSettings.getInstance(project)
        val panel = object : PackagesSearchPanel(project) {
            override fun onDetailsShow(pkg: Package) {
                val detailsComponent = PackageDetails(null)
                val hint = JBPopupFactory.getInstance().createComponentPopupBuilder(detailsComponent, null)
                    .setProject(project)
                    .setDimensionServiceKey(project, DocumentationManager.JAVADOC_LOCATION_AND_SIZE, false)
                    .setResizable(true)
                    .setMovable(true)
                    .setRequestFocus(true)
                    .setTitle("designer.properties.javadoc.title")
                    .createPopup()

                hint.show(RelativePoint(this, Point(0, 0)))
            }
        }

        val showPoint = panel.getShowPoint(project, e.dataContext)
        val popup = JBPopupFactory.getInstance().createComponentPopupBuilder(panel, panel.getPreferableFocusComponent())
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
