package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import mobi.hsz.intellij.packagessearch.utils.RxBus
import mobi.hsz.intellij.packagessearch.utils.events.RegistryChangedEvent

@State(name = "PackagesSearchSettings", storages = [Storage("packagesSearch.xml")])
class PackagesSearchSettings : PersistentStateComponent<PackagesSearchConfig> {
    private var state = PackagesSearchConfig()

    init {
        RxBus.listen(RegistryChangedEvent::class.java).subscribe {
            state.registry = it.context
        }
    }

    override fun getState() = state

    override fun loadState(newState: PackagesSearchConfig) {
        state.registry = newState.registry
        state.version = newState.version
    }

    fun apply(newConfig: PackagesSearchConfig) {
        state.registry = newConfig.registry
        state.version = newConfig.version
    }

    fun getConfig() = PackagesSearchConfig(
        registry = state.registry,
        version = state.version
    )

    companion object {
        fun getInstance() = ServiceManager.getService(PackagesSearchSettings::class.java)!!
    }
}
