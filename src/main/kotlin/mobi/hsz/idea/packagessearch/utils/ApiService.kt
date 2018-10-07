package mobi.hsz.idea.packagessearch.utils

import com.github.kittinunf.fuel.Fuel
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.models.Response

class ApiService {
    companion object {
        fun search(context: RegistryContext, query: String) =
                context.model().let {
                    Fuel.get(it.url(query)).responseObject(it.deserializer())
                }

        fun searchx(context: RegistryContext, query: String, callback: (Response<Package>) -> Unit) =
                context.model().apply {
                    Fuel.get(url(query)).responseObject(deserializer()) { _, _, result ->
                        result.fold({
                            callback(it)
                        }, {
                            println("error")
                            println(it)
                        })
                    }
                }
    }
}