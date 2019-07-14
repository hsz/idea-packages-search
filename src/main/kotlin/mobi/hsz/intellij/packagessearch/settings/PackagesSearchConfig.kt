package mobi.hsz.intellij.packagessearch.settings

import mobi.hsz.intellij.packagessearch.utils.RegistryContext

data class PackagesSearchConfig(
    var registry: RegistryContext = RegistryContext.NPM,
    var version: String? = null
) {
    companion object {
        fun getCurrent(): PackagesSearchConfig {
            val settings = PackagesSearchSettings.getInstance()
            return settings.getConfig()
        }
    }
}
