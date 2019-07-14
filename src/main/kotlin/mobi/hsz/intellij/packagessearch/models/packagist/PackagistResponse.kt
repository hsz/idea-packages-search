package mobi.hsz.intellij.packagessearch.models.packagist

import mobi.hsz.intellij.packagessearch.models.Response

data class PackagistResponse(
    val total: Int = 0,
    val time: String = "",
    val objects: List<PackagistPackage> = emptyList()
) : Response<PackagistPackage>() {
    override val items: List<PackagistPackage>
        get() = objects
}