package mobi.hsz.intellij.packagessearch

import com.intellij.CommonBundle
import org.jetbrains.annotations.PropertyKey
import java.util.ResourceBundle

object PackagesSearchBundle {
    private const val BUNDLE_NAME = "messages.PackagesSearchBundle"
    private val BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME)

    fun message(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any) =
        CommonBundle.message(BUNDLE, key, *params)
}
