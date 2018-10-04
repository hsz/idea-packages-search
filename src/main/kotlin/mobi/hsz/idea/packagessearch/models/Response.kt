package mobi.hsz.idea.packagessearch.models

interface Response<out T> where T : Package {
    val items: List<T>
}
