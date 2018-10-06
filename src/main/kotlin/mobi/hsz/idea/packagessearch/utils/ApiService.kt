package mobi.hsz.idea.packagessearch.utils

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import mobi.hsz.idea.packagessearch.models.NPM.NPMModel
import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.models.Packagist.PackagistModel
import mobi.hsz.idea.packagessearch.models.Response

class ApiService {
    companion object {
        fun search(context: RegistryContext, query: String, callback: (Result<Response<Package>, FuelError>) -> Unit) =
                when (context) {
                    RegistryContext.NPM -> NPMModel()
                    else -> PackagistModel()
                }.apply {
                    Fuel.get(url(query)).responseObject(deserializer()) { req, res, result ->
                        callback(result)
                    }
                }

    }
}