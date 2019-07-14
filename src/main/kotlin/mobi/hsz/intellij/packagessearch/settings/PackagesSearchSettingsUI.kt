package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import mobi.hsz.intellij.packagessearch.utils.RegistryContext
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JPanel

class PackagesSearchSettingsUI {
    private val panel by lazy { PackagesSearchSettingsPanel(PackagesSearchConfig.getCurrent()) }

    fun createConfig() = PackagesSearchConfig(
        registry = panel.registryModel.selected!!,
        version = panel.versionField.text
    )

    fun isModified() = PackagesSearchSettings.getInstance().state != createConfig()

    fun createComponent() = panel

    fun reset() {
        val state = PackagesSearchSettings.getInstance().state
        panel.registryModel.selectedItem = state.registry
        panel.versionField.text = state.version
    }

    inner class PackagesSearchSettingsPanel(config: PackagesSearchConfig) : JBPanel<PackagesSearchSettingsPanel>() {
        val registryModel = CollectionComboBoxModel(RegistryContext.values().toList())
        val registryField = ComboBox<RegistryContext>(registryModel)
        val versionField = JBTextField("version")

        init {
            layout = GridBagLayout()
            addElement(registryField, gridx = 0, gridy = 1)
            addElement(versionField, gridx = 0, gridy = 1)
            addElement(JPanel(), gridx = 0, gridy = 4, weightx = 1.0, weighty = 1.0)
            initValues(config)
        }

        private fun addElement(
            element: Component,
            gridx: Int = 0,
            gridy: Int = 0,
            weightx: Double = 1.0,
            weighty: Double = 0.0,
            anchor: Int = GridBagConstraints.NORTH
        ) {
            val constraints = GridBagConstraints()
            constraints.fill = GridBagConstraints.HORIZONTAL
            constraints.gridx = gridx
            constraints.gridy = gridy
            constraints.weightx = weightx
            constraints.weighty = weighty
            constraints.insets = Insets(5, 5, 5, 5)
            constraints.anchor = anchor
            add(element, constraints)
        }

        private fun initValues(config: PackagesSearchConfig) {
            registryModel.selectedItem = config.registry
            versionField.text = config.version
        }
    }
}