package foocafe.org.foocafe

import android.content.Context
import android.text.TextUtils

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceGenerator {

    internal val API_BASE_URL = "http://foocafe.org"
    private var httpClient = OkHttpClient.Builder()

    private var builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

    private fun <S> createService(
            serviceClass: Class<S>, authToken: String): S? {
        var retrofit: Retrofit? = null
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = AuthenticationInterceptor(authToken)

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)

                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }
        return if (retrofit != null) retrofit.create(serviceClass) else null
    }

    fun <S> createService(serviceClass: Class<S>, accessToken: String?, c: Context): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        if (accessToken != null) {
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                val requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-type", "application/json")
                        .header("Authorization",
                                "Bearer" + " " + accessToken)
                        .method(original.method(), original.body())

                val request = requestBuilder.build()
                chain.proceed(request)
            }
        }
        val client = httpClient.build()
        val retrofit = builder.client(client).build()
        return retrofit.create(serviceClass)
    }
}