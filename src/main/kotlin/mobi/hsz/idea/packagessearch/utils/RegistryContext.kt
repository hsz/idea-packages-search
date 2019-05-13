package mobi.hsz.idea.packagessearch.utils

import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import mobi.hsz.idea.packagessearch.models.npm.NpmModel
import mobi.hsz.idea.packagessearch.models.packagist.PackagistModel
import javax.swing.Icon

enum class RegistryContext {
    DOCKER, MAVEN, NPM, PACKAGIST, PYPI;

    fun model() = when (this) {
        DOCKER-> NpmModel()
        MAVEN -> NpmModel()
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
