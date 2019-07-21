package mobi.hsz.intellij.packagessearch.models.docker

import com.github.kittinunf.fuel.core.ResponseDeserializable
import mobi.hsz.intellij.packagessearch.models.Model
import mobi.hsz.intellij.packagessearch.models.Response

class DockerModel : Model<DockerPackage>() {
    override fun url(query: String, limit: Int) =
        "https://hub.docker.com/api/content/v1/products/search?page_size=$limit&q=$query&type=image"

    override fun deserializer(): ResponseDeserializable<Response<DockerPackage>> =
        Deserializer(DockerResponse::class.java)
}
