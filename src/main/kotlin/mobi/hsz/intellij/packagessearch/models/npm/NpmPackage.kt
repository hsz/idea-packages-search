package mobi.hsz.intellij.packagessearch.models.npm

import mobi.hsz.intellij.packagessearch.models.Package

data class NpmPackage(
    val `package`: PackageLeaf
) : Package {
    override val name: String
        get() = `package`.name
    override val description: String
        get() = `package`.description
    override val version: String
        get() = `package`.version
}

data class PackageLeaf(
    val name: String,
    val version: String,
    val description: String
)
