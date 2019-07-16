package mobi.hsz.intellij.packagessearch.searcheverywhere

import com.github.kittinunf.result.success
import com.intellij.ide.DataManager
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributor
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFactory
import com.intellij.ide.actions.searcheverywhere.SearchEverywhereContributorFilter
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.TimeoutUtil
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import mobi.hsz.intellij.packagessearch.PackagesSearchBundle
import mobi.hsz.intellij.packagessearch.models.Package
import mobi.hsz.intellij.packagessearch.settings.PackagesSearchApplicationConfig
import mobi.hsz.intellij.packagessearch.settings.PackagesSearchProjectConfig
import mobi.hsz.intellij.packagessearch.utils.ApiService
import mobi.hsz.intellij.packagessearch.utils.Constants
import mobi.hsz.intellij.packagessearch.utils.RegistryContext
import org.jetbrains.annotations.NotNull
import java.awt.BorderLayout
import java.awt.Component
import java.util.function.Function
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.JPanel
import javax.swing.ListCellRenderer

class PackagesSearchEverywhereContributor(
    private val project: Project,
    private val contextComponent: Component
) : SearchEverywhereContributor<RegistryContext> {
    private val projectConfig = PackagesSearchProjectConfig.getCurrent(project)
    private val applicationConfig = PackagesSearchApplicationConfig.getCurrent()
    private var query = ""

    override fun fetchElements(
        pattern: String,
        everywhere: Boolean,
        filter: SearchEverywhereContributorFilter<RegistryContext>?,
        progressIndicator: ProgressIndicator,
        consumer: Function<Any, Boolean>
    ) {
        if (progressIndicator.isCanceled) {
            return
        }

        query = pattern
        TimeoutUtil.sleep(Constants.SEARCH_DELAY)
        if (query == pattern) {
            ApiService.search(projectConfig.registry, pattern, applicationConfig.limit).success {
                it.items.forEach { pkg -> consumer.apply(pkg) }
            }
        }
    }

    override fun getAdvertisement() = projectConfig.registry.name

    override fun getDataForItem(@NotNull element: Any, @NotNull dataId: String): Nothing? = null

    override fun getElementsRenderer(list: JList<*>): ListCellRenderer<*> = PackageRenderer(project)

    override fun getFullGroupName() = PackagesSearchBundle.message("searchEverywhere.fullGroupName")

    override fun getGroupName() = PackagesSearchBundle.message("searchEverywhere.groupName")

    override fun getSearchProviderId() = "PackagesSearchEverywhereContributor"

    override fun getSortWeight() = 500

    override fun includeNonProjectItemsText() = PackagesSearchBundle.message("searchEverywhere.includeGitHubRegistry")

    override fun isShownInSeparateTab() = true

    override fun showInFindResults() = false

    override fun processSelectedItem(@NotNull selected: Any, modifiers: Int, @NotNull text: String): Boolean {
        val pkg = selected as Package
        val group = ActionManager.getInstance().getAction("PackagesSearchBehaviors") as ActionGroup
        val dataContext = DataManager.getInstance().getDataContext(contextComponent)

        JBPopupFactory.getInstance().createActionGroupPopup(
            pkg.name,
            group,
            dataContext,
            JBPopupFactory.ActionSelectionAid.NUMBERING,
            true
        ).showCenteredInCurrentWindow(project)

        // modifiers = 1 -> SHIFT
        // modifiers = 2 -> CTRL
        // modifiers = 8 -> ALT
        return false
    }

    private class PackageRenderer(private val project: Project) : DefaultListCellRenderer() {
        private val cell = object : JPanel(BorderLayout()) {
            private val nameLabel = JBLabel()
            private val versionLabel = JBLabel()
            private val descriptionLabel = JBLabel()

            init {
                isOpaque = true
                border = JBUI.Borders.merge(
                    JBUI.Borders.empty(5, 25, 5, 15),
                    JBUI.Borders.customLine(JBColor.GRAY.darker(), 0, 0, 1, 0),
                    true
                )

                add(nameLabel.apply {
                    font = JBUI.Fonts.label().asBold()
                }, BorderLayout.WEST)

                add(versionLabel.apply {
                    fontColor = UIUtil.FontColor.BRIGHTER
                    font = UIUtil.getFont(UIUtil.FontSize.SMALL, font)
                }, BorderLayout.EAST)

                add(descriptionLabel.apply {
                    border = JBEmptyBorder(3, 0, 0, 0)
                    fontColor = UIUtil.FontColor.BRIGHTER
                    font = UIUtil.getFont(UIUtil.FontSize.SMALL, font)
                }, BorderLayout.SOUTH)
            }

            fun withData(pkg: Package, selected: Boolean): JPanel {
                background = UIUtil.getListBackground(selected, true)

                nameLabel.text = pkg.name
                versionLabel.text = pkg.version
                descriptionLabel.text = StringUtil.notNullize(pkg.description)

                return this
            }
        }

        override fun getListCellRendererComponent(
            list: JList<*>,
            value: Any,
            index: Int,
            selected: Boolean,
            hasFocus: Boolean
        ) = cell.withData(value as Package, selected)
    }

    class Factory : SearchEverywhereContributorFactory<RegistryContext> {
        override fun createFilter(initEvent: AnActionEvent?): Nothing? = null

        @NotNull
        override fun createContributor(@NotNull initEvent: AnActionEvent) =
            PackagesSearchEverywhereContributor(
                initEvent.project!!,
                initEvent.getData(PlatformDataKeys.CONTEXT_COMPONENT)!!
            )
    }
}
