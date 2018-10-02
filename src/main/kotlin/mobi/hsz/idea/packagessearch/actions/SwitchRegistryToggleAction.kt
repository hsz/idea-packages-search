package mobi.hsz.idea.packagessearch.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.util.Comparing
import mobi.hsz.idea.packagessearch.utils.RegistryContext

class SwitchRegistryToggleAction(context: RegistryContext) : ToggleAction(context.toString()) {
    override fun isSelected(e: AnActionEvent): Boolean {
        return Comparing.equal("NPM", templatePresentation.text)
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        if (state) {
//            mySelectedContextName = templatePresentation.text
//            scheduleResultsUpdate()
        }
    }
}