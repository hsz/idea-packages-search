package mobi.hsz.intellij.packagessearch.utils

import mobi.hsz.intellij.packagessearch.PackagesSearchBundle
import mobi.hsz.intellij.packagessearch.models.docker.DockerModel
import mobi.hsz.intellij.packagessearch.models.maven.MavenModel
import mobi.hsz.intellij.packagessearch.models.npm.NpmModel
import mobi.hsz.intellij.packagessearch.models.packagist.PackagistModel
import javax.swing.Icon

enum class RegistryContext {
    DOCKER, MAVEN, NPM, PACKAGIST, PYPI;

    fun model() = when (this) {
        DOCKER-> DockerModel()
        MAVEN -> MavenModel()
        NPM -> NpmModel()
        PACKAGIST -> PackagistModel()
        PYPI -> NpmModel()
    }

    // TODO provide real icons
    fun icon(): Icon = when(this) {
        DOCKER -> Icons.DOCKER
        MAVEN -> Icons.MAVEN
        NPM -> Icons.NPM
        PACKAGIST -> Icons.PACKAGIST
        PYPI -> Icons.PYPI
    }

    override fun toString() = PackagesSearchBundle.message("repository.${name.toLowerCase()}.name")
}
