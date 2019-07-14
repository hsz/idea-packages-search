package mobi.hsz.intellij.packagessearch.models.npm

import mobi.hsz.intellij.packagessearch.models.Response

data class NpmResponse(
    val total: Int = 0,
    val results: List<NpmPackage> = emptyList()
) : Response<NpmPackage>() {
    override val items: List<NpmPackage>
        get() = results
}