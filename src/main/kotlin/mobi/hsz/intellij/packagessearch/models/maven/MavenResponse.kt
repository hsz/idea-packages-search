package mobi.hsz.intellij.packagessearch.models.maven

import mobi.hsz.intellij.packagessearch.models.Response

data class MavenResponse(
    val response: ResponseLeaf
) : Response<MavenPackage>() {
    override val items: List<MavenPackage>
        get() = response.docs
}

data class ResponseLeaf(
    val numFound: Int = 0,
    val start: Int = 0,
    val docs: List<MavenPackage> = emptyList()
)
