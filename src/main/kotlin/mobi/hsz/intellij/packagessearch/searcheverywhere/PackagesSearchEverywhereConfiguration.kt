package mobi.hsz.intellij.packagessearch.searcheverywhere

import com.intellij.ide.util.gotoByName.ChooseByNameFilterConfiguration
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.project.Project
import mobi.hsz.intellij.packagessearch.utils.RegistryContext

@State(name = "PackagesSearchEverywhereConfiguration", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class PackagesSearchEverywhereConfiguration : ChooseByNameFilterConfiguration<RegistryContext>() {

    override fun nameForElement(type: RegistryContext?) = type?.name

    companion object {
        fun getInstance(project: Project) = ServiceManager.getService(project, PackagesSearchEverywhereConfiguration::class.java)!!
    }

}