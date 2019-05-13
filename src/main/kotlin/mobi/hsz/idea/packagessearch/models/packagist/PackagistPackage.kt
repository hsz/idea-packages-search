package mobi.hsz.idea.packagessearch.models.packagist

import mobi.hsz.idea.packagessearch.models.Package
import mobi.hsz.idea.packagessearch.models.npm.PackageLeaf

data class PackagistPackage(
    val `package`: PackageLeaf
) : Package {
    override val name: String
        get() = `package`.name
    override val description: String
        get() = `package`.description
    override val version: String
        get() = `package`.version
}
