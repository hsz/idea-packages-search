package mobi.hsz.idea.packagessearch.models.npm

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.idea.packagessearch.models.Model
import mobi.hsz.idea.packagessearch.models.Response

class NpmModel : Model<NpmPackage>() {
    override fun url(query: String) = "https://api.npms.io/v2/search?q=$query&size=100"

    override fun deserializer(): ResponseDeserializable<Response<NpmPackage>> =
        Deserializer(NpmResponse::class.java)
}