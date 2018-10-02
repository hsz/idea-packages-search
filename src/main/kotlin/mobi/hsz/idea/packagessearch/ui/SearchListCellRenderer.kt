package mobi.hsz.idea.packagessearch.ui

import com.intellij.ui.ColoredListCellRenderer
import com.intellij.util.Icons
import javax.swing.JList

class SearchListCellRenderer : ColoredListCellRenderer<String>() {
    override fun customizeCellRenderer(list: JList<out String>, value: String?, index: Int, selected: Boolean, hasFocus: Boolean) {
        icon = Icons.ADD_ICON
        setPaintFocusBorder(false)

        append("xxx")
    }
}