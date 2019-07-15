package mobi.hsz.intellij.packagessearch.settings

data class PackagesSearchApplicationConfig(
    var limit: Int = 15
) {
    companion object {
        fun getCurrent(): PackagesSearchApplicationConfig = PackagesSearchApplicationSettings.getInstance().getConfig()
    }
}
