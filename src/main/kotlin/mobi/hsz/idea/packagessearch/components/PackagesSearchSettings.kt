package mobi.hsz.idea.packagessearch.components

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "PackagesSearchSettings", storages = [Storage("packagesSearch.xml")])
class PackagesSearchSettings : PersistentStateComponent<PackagesSearchSettings.State> {
    data class State(
            var version: String? = null
    )

    private var state: State = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): PackagesSearchSettings =
                ServiceManager.getService<PackagesSearchSettings>(PackagesSearchSettings::class.java)
    }
}
