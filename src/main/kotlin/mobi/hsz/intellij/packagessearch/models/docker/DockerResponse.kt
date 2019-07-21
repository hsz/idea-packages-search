package mobi.hsz.intellij.packagessearch.models.docker

import com.intellij.util.containers.ContainerUtil
import mobi.hsz.intellij.packagessearch.models.Response

data class DockerResponse(
    val page_size: Int,
    val next: String,
    val previous: String,
    val page: Int = 0,
    val count: Int = 0,
    val summaries: List<DockerPackage> = emptyList()
) : Response<DockerPackage>() {
    override val items: List<DockerPackage>
        get() = ContainerUtil.notNullize(summaries)
}
