package mobi.hsz.intellij.packagessearch.models.packagist

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.intellij.packagessearch.models.Model
import mobi.hsz.intellij.packagessearch.models.Response

class PackagistModel : Model<PackagistPackage>() {
    override fun url(query: String, limit: Int) = "https://registry.npmjs.org/-/v1/search?text=$query&size=$limit"

    override fun deserializer(): ResponseDeserializable<Response<PackagistPackage>> =
        Deserializer(PackagistResponse::class.java)
}
