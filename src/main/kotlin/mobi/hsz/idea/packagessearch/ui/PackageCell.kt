package mobi.hsz.idea.packagessearch.ui

import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBEmptyBorder
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.packagessearch.models.Package
import java.awt.BorderLayout

class PackageCell(pkg: Package) : NonOpaquePanel(BorderLayout()) {
    init {
        border = JBUI.Borders.merge(
            JBUI.Borders.empty(5),
            JBUI.Borders.customLine(JBColor.GRAY.darker(), 1, 0, 0, 0),
            true
        )

        add(JBLabel(pkg.name).apply {
            font = JBUI.Fonts.label().asBold()
        }, BorderLayout.WEST)

        add(JBLabel(pkg.version).apply {
            fontColor = UIUtil.FontColor.BRIGHTER
            font = UIUtil.getFont(UIUtil.FontSize.SMALL, font)
        }, BorderLayout.EAST)

        add(JBLabel(StringUtil.notNullize(pkg.description)).apply {
            border = JBEmptyBorder(3, 0, 0, 0)
            fontColor = UIUtil.FontColor.BRIGHTER
            font = UIUtil.getFont(UIUtil.FontSize.SMALL, font)
        }, BorderLayout.SOUTH)
    }
}
