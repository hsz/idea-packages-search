package mobi.hsz.idea.packagessearch.actions

import com.intellij.find.FindBundle
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.ActionButton
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.util.SystemInfo
import mobi.hsz.idea.packagessearch.utils.RegistryContext
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

class RegistryFilterPopupAction : AnAction(FindBundle.message("find.popup.show.filter.popup"), "Description", AllIcons.General.MoreTabs) {
    private val mySwitchContextGroup: DefaultActionGroup
    lateinit var filterContextButton: ActionButton

    init {
        shortcutSet = CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_F, when {
            SystemInfo.isMac -> InputEvent.CTRL_DOWN_MASK or InputEvent.ALT_DOWN_MASK
            else -> InputEvent.ALT_DOWN_MASK
        }))

        mySwitchContextGroup = DefaultActionGroup().apply {
            isPopup = true
            RegistryContext.values().iterator().forEach {
                add(SwitchRegistryToggleAction(it))
            }
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        if (PlatformDataKeys.CONTEXT_COMPONENT.getData(e.dataContext) == null) return

        val listPopup = JBPopupFactory.getInstance().createActionGroupPopup(null, mySwitchContextGroup, e.dataContext, false, null, 10)
        listPopup.showUnderneathOf(filterContextButton)
    }
}