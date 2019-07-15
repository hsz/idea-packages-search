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
        state.version = newState.version
    }

    fun apply(newConfig: PackagesSearchApplicationConfig) {
        state.version = newConfig.version
    }

    fun getConfig() = PackagesSearchApplicationConfig(
        version = state.version
    )

    companion object {
        fun getInstance() = ServiceManager.getService(PackagesSearchApplicationSettings::class.java)!!
    }
}
