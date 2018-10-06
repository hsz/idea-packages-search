package mobi.hsz.idea.packagessearch.models

data class PackagistPackage(
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
