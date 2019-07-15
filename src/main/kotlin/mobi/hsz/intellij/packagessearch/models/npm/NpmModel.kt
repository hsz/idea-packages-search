package mobi.hsz.intellij.packagessearch.models.npm

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.intellij.packagessearch.models.Model
import mobi.hsz.intellij.packagessearch.models.Response

class NpmModel : Model<NpmPackage>() {
    override fun url(query: String, limit: Int) = "https://api.npms.io/v2/search?q=$query&size=$limit"

    override fun deserializer(): ResponseDeserializable<Response<NpmPackage>> =
        Deserializer(NpmResponse::class.java)
}
