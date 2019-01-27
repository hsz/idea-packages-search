package mobi.hsz.idea.packagessearch.ui

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import mobi.hsz.idea.packagessearch.models.Package
import java.awt.BorderLayout

class PackageDetails(pkg: Package?) : JBPanel<JBPanel<*>>(BorderLayout()) {
    init {
        add(JBLabel("yy"))
    }
}