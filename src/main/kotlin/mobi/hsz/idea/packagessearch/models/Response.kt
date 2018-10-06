package mobi.hsz.idea.packagessearch.models

abstract class Response<out T> where T : Package {
    abstract val items: List<T>
}
