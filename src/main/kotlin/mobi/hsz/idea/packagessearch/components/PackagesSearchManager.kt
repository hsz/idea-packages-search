package mobi.hsz.idea.packagessearch.components

import com.intellij.ide.ui.UISettings
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.openapi.wm.impl.IdeFrameImpl
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.packagessearch.ui.components.PackagesSearchUI
import java.awt.Point
import java.awt.event.KeyEvent


class PackagesSearchManager {
    companion object {
        fun getInstance(project: Project): PackagesSearchManager {
            return ServiceManager.getService(project, PackagesSearchManager::class.java)
        }
    }

    private lateinit var packagesSearchUI: PackagesSearchUI
    private lateinit var balloon: JBPopup

    fun show(project: Project, dataContext: DataContext) {
        packagesSearchUI = createView(project)

        val factory = JBPopupFactory.getInstance()
        balloon = factory.createComponentPopupBuilder(packagesSearchUI, packagesSearchUI.getSearchField())
                .setProject(project)
                .setModalContext(false)
                .setCancelOnClickOutside(true)
                .setRequestFocus(true)
                .setCancelCallback {
                    //                    saveSearchText()
                    true
                }
                .setKeyEventHandler {
                    if (it.id == KeyEvent.KEY_PRESSED && it.keyCode == KeyEvent.VK_ESCAPE) {
                        balloon.cancel()
                    }
                    true
                }
                .addUserData("SIMPLE_WINDOW")
                .setResizable(true)
                .setMovable(true)
                .setDimensionServiceKey(project, "xxx", true)
                .setLocateWithinScreenBounds(false)
                .createPopup()

        val window = WindowManager.getInstance().suggestParentWindow(project)
        val parent = UIUtil.findUltimateParent(window)
        val showPoint: RelativePoint
        if (parent != null) {
            println("11")
            var height = if (UISettings.instance.showMainToolbar) 135 else 115
            if (parent is IdeFrameImpl && parent.isInFullScreen) {
                height -= 20
            }
            showPoint = RelativePoint(parent, Point((parent.getSize().width - packagesSearchUI.getPreferredSize().width) / 2, height))
        } else {
            println("222")
            showPoint = JBPopupFactory.getInstance().guessBestPopupLocation(dataContext)
        }

        balloon.show(showPoint)

    }

    private fun createView(project: Project): PackagesSearchUI {
        val ui = PackagesSearchUI(project)

        return ui
    }
}