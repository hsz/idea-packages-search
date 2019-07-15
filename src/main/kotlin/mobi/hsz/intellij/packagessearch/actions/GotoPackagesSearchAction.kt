package mobi.hsz.intellij.packagessearch.actions

import com.intellij.ide.actions.GotoActionBase
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.util.registry.Registry
import mobi.hsz.intellij.packagessearch.searcheverywhere.PackagesSearchEverywhereContributor

class GotoPackagesSearchAction : GotoActionBase() {
    override fun actionPerformed(e: AnActionEvent) {
        if (Registry.`is`("new.search.everywhere")) {
            showInSearchEverywherePopup(PackagesSearchEverywhereContributor::class.java.simpleName, e, true)
        } else {
            super.actionPerformed(e)
        }
    }

    override fun gotoActionPerformed(e: AnActionEvent) {
        TODO("not implemented")
    }

    override fun getTemplateText(): String? {
        return "xxx"
    }
}
