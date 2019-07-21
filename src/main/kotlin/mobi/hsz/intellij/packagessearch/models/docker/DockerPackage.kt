package mobi.hsz.intellij.packagessearch.models.docker

import mobi.hsz.intellij.packagessearch.models.Package
import java.util.Date

data class DockerPackage(
    override val name: String,
    override val version: String = "unknown",
    val id: String,
    val slug: String,
    val type: String,
    val publisher: PublisherLeaf,
    val created_at: Date,
    val updated_at: Date,
    val short_description: String,
    val source: String,
    val popularity: Int,
    val categories: List<LabelLeaf>,
    val operating_systems: List<LabelLeaf>,
    val architectures: List<LabelLeaf>,
    val logo_url: LogoLeaf,
    val certification_status: String,
    val star_count: Int,
    val filter_type: String
) : Package {
    override val description: String
        get() = short_description
}

data class PublisherLeaf(
    val id: String,
    val name: String
)

data class LabelLeaf(
    val name: String,
    val label: String
)

data class LogoLeaf(
    val small: String,
    val `small@2x`: String
)
