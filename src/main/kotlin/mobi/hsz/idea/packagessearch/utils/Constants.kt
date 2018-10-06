package mobi.hsz.idea.packagessearch.utils

import com.intellij.ui.Gradient
import com.intellij.ui.JBColor

class Constants {
    companion object {
        internal const val API_ENDPOINT = "https://api.nodesecurity.io"
        internal const val SEARCH_DELAY = 300L
        internal val GRADIENT = Gradient(
                JBColor(0x65F065, 0x455C3F),
                JBColor(0x3CCC2F, 0x365735)
        )
    }
}
