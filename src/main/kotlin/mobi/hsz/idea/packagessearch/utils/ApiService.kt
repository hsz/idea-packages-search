package mobi.hsz.idea.packagessearch.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import mobi.hsz.idea.packagessearch.models.NpmResponse

class ApiService {
    companion object {
        private fun registryUrl(context: RegistryContext, query: String) = when (context) {
            RegistryContext.NPM -> "https://registry.npmjs.org/-/v1/search?text=$query&size=10"
            else -> ""
        }

        private fun registryDeserializer(context: RegistryContext) = when (context) {
            RegistryContext.NPM -> NpmResponse.Deserializer()
            else -> NpmResponse.Deserializer()
        }

        fun search(context: RegistryContext, query: String, callback: (Result<NpmResponse, Exception>) -> Unit) =
                Fuel.get(registryUrl(context, query)).responseObject(registryDeserializer(context)) { req, res, result ->
                    callback(result)
                }
    }
}