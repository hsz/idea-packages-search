package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import mobi.hsz.intellij.packagessearch.PackagesSearchBundle
import mobi.hsz.intellij.packagessearch.utils.RegistryContext
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JPanel

class PackagesSearchSettingsUI(private val project: Project) {
    private val panel by lazy {
        PackagesSearchSettingsPanel(
            PackagesSearchProjectConfig.getCurrent(project),
            PackagesSearchApplicationConfig.getCurrent()
        )
    }

    fun createComponent() = panel

    fun createConfig() =
        PackagesSearchProjectConfig(
            registry = panel.registryModel.selected!!
        ) to PackagesSearchApplicationConfig(
            version = panel.versionField.text

        )

    fun isModified(): Boolean {
        val (projectConfig, applicationConfig) = createConfig()
        return PackagesSearchProjectSettings.getInstance(project).state != projectConfig
            || PackagesSearchApplicationSettings.getInstance().state != applicationConfig
    }

    fun reset() {
        val projectState = PackagesSearchProjectSettings.getInstance(project).state
        panel.registryModel.selectedItem = projectState.registry

        val applicationState = PackagesSearchApplicationSettings.getInstance().state
        panel.versionField.text = applicationState.version
    }

    inner class PackagesSearchSettingsPanel(
        projectConfig: PackagesSearchProjectConfig,
        applicationConfig: PackagesSearchApplicationConfig
    ) :
        JBPanel<PackagesSearchSettingsPanel>() {
        private val projectSettingsPanel = JBPanel<PackagesSearchSettingsPanel>(GridBagLayout())
        private val globalSettingsPanel = JBPanel<PackagesSearchSettingsPanel>(GridBagLayout())

        val registryModel = CollectionComboBoxModel(RegistryContext.values().toList())
        val registryField = ComboBox<RegistryContext>(registryModel)
        val versionField = JBTextField("version")

        init {
            layout = GridBagLayout()

            projectSettingsPanel.apply {
                layout = GridBagLayout()
                border = IdeBorderFactory.createTitledBorder(PackagesSearchBundle.message("settings.projectSection"))

                addRow(
                    this, listOf(
                        JBLabel("Registry"),
                        registryField
                    ), 0
                )
            }

            globalSettingsPanel.apply {
                layout = GridBagLayout()
                border = IdeBorderFactory.createTitledBorder(PackagesSearchBundle.message("settings.appSection"))

                addRow(
                    this, listOf(
                        JBLabel("Version"),
                        versionField
                    ), 0
                )
                addRow(
                    this, listOf(
                        JBLabel("Version XXXXXX"),
                        JBLabel("xx")
                    ), 1
                )
            }

            addElement(this, projectSettingsPanel, 0, 0)
            addElement(this, globalSettingsPanel, 0, 1)
            addElement(this, JPanel(), 0, 4, 1.0, 1.0)

            initValues(projectConfig, applicationConfig)
        }

        private fun addRow(container: JPanel, elements: List<Component>, y: Int = 0) =
            elements.forEachIndexed { index, element ->
                val w = if (index == 0) 0.0 else 1.0
                addElement(container, element, index, y, w, 0.0, GridBagConstraints.CENTER)
            }

        private fun addElement(
            container: JPanel,
            element: Component,
            x: Int = 0,
            y: Int = 0,
            weightx: Double = 0.0,
            weighty: Double = 0.0,
            anchor: Int = GridBagConstraints.NORTH,
            fill: Int = GridBagConstraints.HORIZONTAL,
            insets: Insets = Insets(5, 5, 5, 5)
        ) = container.add(
            element,
            GridBagConstraints(x, y, 1, 1, weightx, weighty, anchor, fill, insets, 0, 0)
        )

        private fun initValues(
            projectConfig: PackagesSearchProjectConfig,
            applicationConfig: PackagesSearchApplicationConfig
        ) {
            // projectConfig
            registryModel.selectedItem = projectConfig.registry

            // applicationConfig
            versionField.text = applicationConfig.version
        }
    }
}
