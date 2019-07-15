package mobi.hsz.intellij.packagessearch.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import mobi.hsz.intellij.packagessearch.utils.RxBus
import mobi.hsz.intellij.packagessearch.utils.events.RegistryChangedEvent

@State(name = "PackagesSearchProjectSettings", storages = [Storage("packagesSearch.xml")])
class PackagesSearchProjectSettings(val project: Project) : PersistentStateComponent<PackagesSearchProjectConfig> {
    private var state = PackagesSearchProjectConfig()

    init {
        RxBus.listen(RegistryChangedEvent::class.java).subscribe {
            state.registry = it.context
        }
    }

    override fun getState() = state

    override fun loadState(newState: PackagesSearchProjectConfig) {
        state.registry = newState.registry
    }

    fun apply(newConfig: PackagesSearchProjectConfig) {
        state.registry = newConfig.registry
    }

    fun getConfig() = PackagesSearchProjectConfig(
        registry = state.registry
    )

    companion object {
        fun getInstance(project: Project) =
            ServiceManager.getService(project, PackagesSearchProjectSettings::class.java)!!
    }
}
