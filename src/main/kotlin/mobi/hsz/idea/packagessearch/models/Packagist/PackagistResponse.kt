package mobi.hsz.idea.packagessearch.models.Packagist

import mobi.hsz.idea.packagessearch.models.Response

data class PackagistResponse(
        val total: Int = 0,
        val time: String = "",
        val objects: List<PackagistPackage> = emptyList()
) : Response<PackagistPackage>() {
    override val items: List<PackagistPackage>
        get() = objects
}