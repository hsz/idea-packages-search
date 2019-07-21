package mobi.hsz.intellij.packagessearch.models.maven

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.intellij.packagessearch.models.Model
import mobi.hsz.intellij.packagessearch.models.Response

class MavenModel : Model<MavenPackage>() {
    override fun url(query: String, limit: Int) =
        "https://search.maven.org/solrsearch/select?q=$query&rows=$limit&wt=json"

    override fun deserializer(): ResponseDeserializable<Response<MavenPackage>> =
        Deserializer(MavenResponse::class.java)
}
