package mobi.hsz.idea.packagessearch.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class NpmResponse(
        val total: Int = 0,
        val time: String = "",
        val objects: List<NpmPackage> = emptyList()
) : Response<NpmPackage> {
    override val items: List<NpmPackage>
        get() = objects

    class Deserializer : ResponseDeserializable<NpmResponse> {
        override fun deserialize(content: String) = Gson().fromJson(content, NpmResponse::class.java)!!
    }

}