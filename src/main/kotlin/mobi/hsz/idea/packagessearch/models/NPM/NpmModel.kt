package mobi.hsz.idea.packagessearch.models.NPM

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.idea.packagessearch.models.Model
import mobi.hsz.idea.packagessearch.models.Response

class NpmModel : Model<NpmPackage>() {
    override fun url(query: String) = "https://registry.npmjs.org/-/v1/search?text=$query&size=10"

    override fun deserializer(): ResponseDeserializable<Response<NpmPackage>> =
        Deserializer(NpmResponse::class.java)
}