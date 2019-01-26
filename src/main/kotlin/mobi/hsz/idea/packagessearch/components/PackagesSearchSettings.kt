package mobi.hsz.idea.packagessearch.components

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import mobi.hsz.idea.packagessearch.utils.RegistryContext
import mobi.hsz.idea.packagessearch.utils.RxBus
import mobi.hsz.idea.packagessearch.utils.events.RegistryChangedEvent

@State(name = "PackagesSearchSettings", storages = [Storage("packagesSearch.xml")])
class PackagesSearchSettings : PersistentStateComponent<PackagesSearchSettings.State> {
    data class State(
        var version: String? = null,
        var registry: RegistryContext = RegistryContext.NPM
    )

    private var state = State()

    init {
        RxBus.listen(RegistryChangedEvent::class.java).subscribe {
            state.registry = it.context
        }
    }

    override fun getState() = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(project: Project) = ServiceManager.getService(project, PackagesSearchSettings::class.java)!!
    }
}
