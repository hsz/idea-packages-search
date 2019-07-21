package mobi.hsz.intellij.packagessearch.utils

import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet

class ApiService {
    companion object {
        fun search(
            context: RegistryContext,
            query: String,
            limit: Int
        ) =
            context.model().let {
                it.url(query, limit).httpGet()
                    .header(Headers.USER_AGENT, Constants.USER_AGENT)
                    .responseObject(it.deserializer()).third
            }
    }
}
