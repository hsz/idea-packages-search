package mobi.hsz.idea.packagessearch.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.result.Result
import com.intellij.concurrency.JobScheduler
import mobi.hsz.idea.packagessearch.models.NpmResponse
import mobi.hsz.idea.packagessearch.models.Response
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.deferred
import nl.komponents.kovenant.task
import nl.komponents.kovenant.then
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ApiService {
    companion object {
        private val api = ApiService()


        private val debouncedSearch = Debounce({ first, second ->
            api.get(registryUrl(first, second), registryDeserializer(first))
        }, 200)

        private fun registryUrl(context: RegistryContext, query: String) = when (context) {
            RegistryContext.NPM -> "https://registry.npmjs.org/-/v1/search?text=$query&size=10"
            else -> ""
        }

        private fun registryDeserializer(context: RegistryContext) = when (context) {
            RegistryContext.NPM -> NpmResponse.Deserializer()
            else -> NpmResponse.Deserializer()
        }

        fun search(context: RegistryContext, query: String): Promise<Response<*>, Exception> = debouncedSearch(context, query)
    }

    private fun <T : Any> get(url: String, deserializer: ResponseDeserializable<T>): Promise<T, Exception> =
            Fuel.get(url).promise(deserializer)

    private fun <T : Any> post(url: String, body: String, deserializer: ResponseDeserializable<T>): Promise<T, Exception> =
            Fuel.post(url).body(body).promise(deserializer)

    class Debounce<T>(private val fn: (context: RegistryContext, query: String) -> Promise<T, Exception>, private val delay: Int) {
        private var timer: ScheduledFuture<*>? = null

        operator fun invoke(context: RegistryContext, query: String): Promise<T, Exception> {
            val deferred = deferred<T, Exception>()

            if (timer != null) {
                timer!!.cancel(true)
            }

            timer = JobScheduler.getScheduler().schedule(
                    {
                        fn(context, query) then {
                            deferred.resolve(it)
                        } fail {
                            deferred.reject(it)
                        }
                    },
                    delay.toLong(),
                    TimeUnit.MILLISECONDS
            )

            return deferred.promise
        }
    }
}

fun <T : Any> Request.promise(deserializer: ResponseDeserializable<T>): Promise<T, Exception> {
    val deferred = deferred<T, Exception>()
    task { responseObject(deserializer) } success { (_, _, result) ->
        when (result) {
            is Result.Success -> deferred.resolve(result.value)
            is Result.Failure -> deferred.reject(result.error)
        }
    } fail {
        deferred.reject(it)
    }
    return deferred.promise
}
