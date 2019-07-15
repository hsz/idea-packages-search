package mobi.hsz.intellij.packagessearch.settings

data class PackagesSearchApplicationConfig(
    var version: String? = null
) {
    companion object {
        fun getCurrent(): PackagesSearchApplicationConfig = PackagesSearchApplicationSettings.getInstance().getConfig()
    }
}
