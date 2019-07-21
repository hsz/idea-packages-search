package mobi.hsz.intellij.packagessearch.models.npm

import mobi.hsz.intellij.packagessearch.models.Package
import java.util.Date

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
    val scope: String,
    val version: String,
    val description: String,
    val date: Date,
    val links: LinksLeaf,
    val author: AuthorLeaf,
    val publisher: UserLeaf,
    val maintainers: List<UserLeaf>
)

data class LinksLeaf(
    val npm: String,
    val homepage: String,
    val repository: String,
    val bugs: String
)

data class AuthorLeaf(
    val name: String
)

data class UserLeaf(
    val username: String,
    val email: String
)
