package mobi.hsz.idea.packagessearch.models

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

abstract class Response<out T> where T : Package {
    abstract val items: List<T>
    abstract fun getDeserializer(): ResponseDeserializable<Response<T>>

    class Deserializer<T : Package, R : Response<T>>(private val clazz: Class<R>) : ResponseDeserializable<R> {
        override fun deserialize(content: String) = Gson().fromJson(content, clazz)!!
    }
}
