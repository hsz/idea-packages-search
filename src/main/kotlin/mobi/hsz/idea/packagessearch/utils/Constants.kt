package mobi.hsz.idea.packagessearch.utils

import com.intellij.ui.Gradient
import com.intellij.ui.JBColor

class Constants {
    companion object {
        internal const val SEARCH_DELAY = 50L
        internal const val USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.108 Safari/537.36"
        internal val GRADIENT = Gradient(
            JBColor(0x95C083, 0x455C3F),
            JBColor(0x76BB75, 0x365735)
        )
    }
}
