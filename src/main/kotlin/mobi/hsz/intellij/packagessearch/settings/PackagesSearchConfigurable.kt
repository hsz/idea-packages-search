package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.options.SearchableConfigurable
import mobi.hsz.intellij.packagessearch.PackagesSearchBundle
import javax.swing.JComponent

class PackagesSearchConfigurable : SearchableConfigurable {
    private val settingsUI by lazy { PackagesSearchSettingsUI() }

    override fun getId() = "PackagesSearchConfigurable"

    override fun getDisplayName() = PackagesSearchBundle.message("name")

    override fun isModified() = settingsUI.isModified()

    override fun apply() {
        val settings = PackagesSearchSettings.getInstance()
        settings.apply(settingsUI.createConfig())
    }

    override fun reset() {
        settingsUI.reset()
    }

    override fun createComponent(): JComponent? = settingsUI.createComponent()
}