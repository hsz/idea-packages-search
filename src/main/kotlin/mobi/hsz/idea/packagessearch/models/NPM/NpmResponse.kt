package mobi.hsz.idea.packagessearch.models.NPM

import mobi.hsz.idea.packagessearch.models.Response

data class NpmResponse(
        val total: Int = 0,
        val time: String = "",
        val objects: List<NpmPackage> = emptyList()
) : Response<NpmPackage>() {
    override val items: List<NpmPackage>
        get() = objects
}