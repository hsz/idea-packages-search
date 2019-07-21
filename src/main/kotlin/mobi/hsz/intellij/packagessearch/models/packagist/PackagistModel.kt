package mobi.hsz.intellij.packagessearch.models.packagist

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.intellij.packagessearch.models.Model
import mobi.hsz.intellij.packagessearch.models.Response

class PackagistModel : Model<PackagistPackage>() {
    override fun url(query: String, limit: Int) = "https://packagist.org/search.json?q=$query&per_page=$limit"

    override fun deserializer(): ResponseDeserializable<Response<PackagistPackage>> =
        Deserializer(PackagistResponse::class.java)
}
