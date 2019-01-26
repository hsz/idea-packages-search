package mobi.hsz.idea.packagessearch.ui

import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.panels.NonOpaquePanel
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import java.awt.BorderLayout

class Hints : NonOpaquePanel(BorderLayout()) {
    private val hints = listOf(
        PackagesSearchBundle.message("hints.showDetails"),
        PackagesSearchBundle.message("hints.install"),
        PackagesSearchBundle.message("hints.openInBrowser")
    )

    init {
        val label = JBLabel().apply {
            border = IdeBorderFactory.createEmptyBorder(3, 3, 0, 3)
            fontColor = UIUtil.FontColor.BRIGHTER
            font = JBUI.Fonts.smallFont()
            text = hints.random()
        }

        add(label)
    }
}