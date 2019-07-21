package mobi.hsz.intellij.packagessearch.models.packagist

import mobi.hsz.intellij.packagessearch.models.Package

data class PackagistPackage(
    override val name: String,
    override val description: String,
    override val version: String = "unknown",
    val url: String,
    val repository: String,
    val downloads: Int,
    val favers: Int
) : Package
