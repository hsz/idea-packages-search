package mobi.hsz.idea.packagessearch.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import mobi.hsz.idea.packagessearch.models.NpmResponse
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.models.PackagistResponse
import mobi.hsz.idea.packagessearch.models.Response

class ApiService {
    companion object {
        private fun registryUrl(context: RegistryContext, query: String) = when (context) {
            RegistryContext.NPM -> "https://registry.npmjs.org/-/v1/search?text=$query&size=10"
            else -> ""
        }

        fun search(context: RegistryContext, query: String, callback: (Result<Response<Package>, FuelError>) -> Unit) = when (context) {
            RegistryContext.NPM -> NpmResponse()
            else -> PackagistResponse()
        }.apply {
            val des = getDeserializer()
            Fuel.get(registryUrl(context, query)).responseObject(des) { req, res, result ->
                callback(result)
            }
        }

    }
}