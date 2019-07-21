package mobi.hsz.intellij.packagessearch.models.maven

import mobi.hsz.intellij.packagessearch.models.Package

data class MavenPackage(
    override val description: String = "unknown",
    val id: String,
    val g: String,
    val a: String,
    val latestVersion: String,
    val repositoryId: String,
    val p: String,
    val timestamp: Int,
    val versionCount: Int
) : Package {
    override val name: String
        get() = id
    override val version: String
        get() = latestVersion

}

