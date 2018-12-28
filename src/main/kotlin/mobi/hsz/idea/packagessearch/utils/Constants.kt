package mobi.hsz.idea.packagessearch.utils

import com.intellij.ui.Gradient
import com.intellij.ui.JBColor

class Constants {
    companion object {
        internal const val API_ENDPOINT = "https://api.nodesecurity.io"
        internal const val SEARCH_DELAY = 5L
        internal val GRADIENT = Gradient(
                JBColor(0x95C083, 0x455C3F),
                JBColor(0x76BB75, 0x365735)
        )
    }
}
