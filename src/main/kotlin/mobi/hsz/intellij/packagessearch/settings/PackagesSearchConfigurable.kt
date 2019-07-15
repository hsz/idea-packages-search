package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import mobi.hsz.intellij.packagessearch.PackagesSearchBundle

class PackagesSearchConfigurable(private val project: Project) : SearchableConfigurable {
    private val settingsUI by lazy { PackagesSearchSettingsUI(project) }

    override fun getId() = "PackagesSearchConfigurable"

    override fun getDisplayName() = PackagesSearchBundle.message("name")

    override fun isModified() = settingsUI.isModified()

    override fun apply() {
        val (projectConfig, applicationConfig) = settingsUI.createConfig()
        PackagesSearchProjectSettings.getInstance(project).apply(projectConfig)
        PackagesSearchApplicationSettings.getInstance().apply(applicationConfig)
    }

    override fun reset() {
        settingsUI.reset()
    }

    override fun createComponent() = settingsUI.createComponent()
}
