package mobi.hsz.idea.packagessearch.models.NPM

import mobi.hsz.idea.packagessearch.models.Package

data class NpmPackage(
    val `package`: PackageLeaf,
    val score: Score,
    val searchScore: Float
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
    val date: String
//    val links: String,
//    val author: String,
//    val publisher: String,
//    val maintainers: String,
)

class Score(
    val final: Float
//        val detail: asd
)

