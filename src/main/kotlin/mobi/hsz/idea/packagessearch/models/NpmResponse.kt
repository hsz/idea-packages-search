package mobi.hsz.idea.packagessearch.models

import com.github.kittinunf.fuel.core.ResponseDeserializable

data class NpmResponse(
        val total: Int = 0,
        val time: String = "",
        val objects: List<NpmPackage> = emptyList()
) : Response<NpmPackage>() {
    override fun getDeserializer(): ResponseDeserializable<Response<NpmPackage>> =
            Deserializer(NpmResponse::class.java)

    override val items: List<NpmPackage>
        get() = objects
}