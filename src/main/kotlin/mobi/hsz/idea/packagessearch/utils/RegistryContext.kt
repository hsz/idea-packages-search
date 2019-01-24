package mobi.hsz.idea.packagessearch.utils

import mobi.hsz.idea.packagessearch.PackagesSearchBundle
import mobi.hsz.idea.packagessearch.models.NPM.NpmModel
import mobi.hsz.idea.packagessearch.models.Packagist.PackagistModel

enum class RegistryContext {
    MAVEN, NPM, PACKAGIST, PYPI;

    fun model() = when (this) {
        MAVEN -> NpmModel()
        NPM -> NpmModel()
        PACKAGIST -> PackagistModel()
        PYPI -> NpmModel()
    }

    override fun toString() = PackagesSearchBundle.message("repository.${name.toLowerCase()}.name")
}
