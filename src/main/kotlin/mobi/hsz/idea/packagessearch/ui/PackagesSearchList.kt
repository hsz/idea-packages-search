package mobi.hsz.idea.packagessearch.ui

import com.intellij.ui.CollectionListModel
import com.intellij.ui.components.JBList
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.utils.RxBus
import mobi.hsz.idea.packagessearch.utils.events.RequestSearchFocusEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class PackagesSearchList(model: CollectionListModel<Package>) : JBList<Package>(model) {
    init {
        isVisible = false
        installCellRenderer<Package> { pkg -> PackageCell(pkg) }

        addFocusListener(object : FocusListener {
            override fun focusLost(e: FocusEvent?) {
            }

            override fun focusGained(e: FocusEvent?) {
                RxBus.publish(RequestSearchFocusEvent())
            }
        })

        addMouseListener(object : MouseListener {
            override fun mouseReleased(e: MouseEvent?) {
            }

            override fun mouseEntered(e: MouseEvent?) {
            }

            override fun mouseExited(e: MouseEvent?) {
            }

            override fun mousePressed(e: MouseEvent?) {
            }

            override fun mouseClicked(e: MouseEvent?) {
            }
        })
    }
}