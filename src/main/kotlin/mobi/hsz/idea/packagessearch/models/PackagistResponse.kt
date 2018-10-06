package mobi.hsz.idea.packagessearch.models

import com.github.kittinunf.fuel.core.ResponseDeserializable

data class PackagistResponse(
        val total: Int = 0,
        val time: String = "",
        val objects: List<PackagistPackage> = emptyList()
) : Response<PackagistPackage>() {
    override fun getDeserializer(): ResponseDeserializable<Response<PackagistPackage>> =
            Deserializer(PackagistResponse::class.java)

    override val items: List<PackagistPackage>
        get() = objects
}