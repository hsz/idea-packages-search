package mobi.hsz.idea.packagessearch.utils

import mobi.hsz.idea.packagessearch.PackagesSearchBundle

enum class RegistryContext {
    NPM, MAVEN, PACKAGIST, PYPI;

    override fun toString() = PackagesSearchBundle.message("repository.${name.toLowerCase()}.name")
}
