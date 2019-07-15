package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "PackagesSearchSettings", storages = [Storage("packagesSearch.xml")])
class PackagesSearchApplicationSettings : PersistentStateComponent<PackagesSearchApplicationConfig> {
    private var state = PackagesSearchApplicationConfig()

    override fun getState() = state

    override fun loadState(newState: PackagesSearchApplicationConfig) {
        state.limit = newState.limit
    }

    fun apply(newConfig: PackagesSearchApplicationConfig) {
        state.limit = newConfig.limit
    }

    fun getConfig() = PackagesSearchApplicationConfig(
        limit = state.limit
    )

    companion object {
        fun getInstance() = ServiceManager.getService(PackagesSearchApplicationSettings::class.java)!!
    }
}
