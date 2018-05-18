package foocafe.org.foocafe

import java.io.IOException

import okhttp3.Interceptor
import okhttp3.Response

class AuthenticationInterceptor internal constructor(private val authToken: String) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
                .header("Authorization", authToken)

        val request = builder.build()
        return chain.proceed(request)
    }
}