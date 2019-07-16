package mobi.hsz.intellij.packagessearch.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware

class DummyAction : AnAction(), DumbAware {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTemplateText(): String? {
        return "FOO"
    }
}
