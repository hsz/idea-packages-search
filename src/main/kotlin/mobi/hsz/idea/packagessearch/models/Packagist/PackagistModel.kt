package mobi.hsz.idea.packagessearch.models.Packagist

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.idea.packagessearch.models.Model
import mobi.hsz.idea.packagessearch.models.Response

class PackagistModel : Model<PackagistPackage>() {
    override fun url(query: String) = "https://registry.npmjs.org/-/v1/search?text=$query&size=10"

    override fun deserializer(): ResponseDeserializable<Response<PackagistPackage>> =
        Deserializer(PackagistResponse::class.java)
}