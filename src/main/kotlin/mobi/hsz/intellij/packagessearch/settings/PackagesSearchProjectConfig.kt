package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.project.Project
import mobi.hsz.intellij.packagessearch.utils.RegistryContext

data class PackagesSearchProjectConfig(
    var registry: RegistryContext = RegistryContext.NPM
) {
    companion object {
        fun getCurrent(project: Project): PackagesSearchProjectConfig =
            PackagesSearchProjectSettings.getInstance(project).getConfig()
    }
}
