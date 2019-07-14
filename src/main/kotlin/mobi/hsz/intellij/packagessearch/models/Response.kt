package mobi.hsz.intellij.packagessearch.models

abstract class Response<out T> where T : Package {
    abstract val items: List<T>
}
