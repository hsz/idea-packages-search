package mobi.hsz.idea.packagessearch.utils

import awaitObjectResponse
import com.github.kittinunf.fuel.httpGet
import kotlin.coroutines.CoroutineContext

class ApiService {
    companion object {
        suspend fun search(
            context: RegistryContext,
            query: String,
            coroutineContext: CoroutineContext
        ) =
            context.model().let {
                it.url(query).httpGet().awaitObjectResponse(it.deserializer(), coroutineContext).third
            }
    }
}
