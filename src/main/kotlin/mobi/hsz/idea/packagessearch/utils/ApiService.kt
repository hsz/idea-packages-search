package mobi.hsz.idea.packagessearch.utils

import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.coroutines.awaitObjectResponse
import com.github.kittinunf.fuel.httpGet
import kotlin.coroutines.CoroutineContext

class ApiService {
    companion object {
        fun search(
            context: RegistryContext,
            query: String
        ) =
            context.model().let {
                println(it.url(query))
                it.url(query).httpGet().header(
                    Headers.USER_AGENT, Constants.USER_AGENT
                ).responseObject(it.deserializer()).third
            }

        suspend fun searchOld(
            context: RegistryContext,
            query: String,
            coroutineContext: CoroutineContext
        ) =
            context.model().let {
                it.url(query).httpGet().awaitObjectResponse(it.deserializer(), coroutineContext).third
            }
    }
}
