package com.example.makeev.myfirstapplication

import com.example.makeev.myfirstapplication.model.converter.DataConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

object ApiUtils {

    val NETWORK_EXCEPTIONS = Arrays.asList<Class<*>>(
            UnknownHostException::class.java,
            SocketTimeoutException::class.java,
            ConnectException::class.java
    )

    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null
    private var gson: Gson? = null
    private var api: AcademyApi? = null
    private val userSelectionInterceptor = UserSelectionInterceptor()

    fun getBasicAuthClient(email: String, password: String, newInstance: Boolean): OkHttpClient {
        if (newInstance || client == null) {
            val builder: OkHttpClient.Builder = OkHttpClient().newBuilder()
            builder.authenticator { _, response ->
                if (responseCount(response) >= 3) {
                    return@authenticator null // If we've failed 3 times, give up. - in real life, never give up!!
                }
                val credential = Credentials.basic(email, password)
                response.request()
                        .newBuilder()
                        .header("Authorization", credential)
                        .build()
            }
            builder.addInterceptor(userSelectionInterceptor)
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
            }
            client = builder.build()
        }
        return client!!
    }

    //Используется для ограничения ошибок ответа
    private fun responseCount(response: Response?): Int {
        var response = response
        var result = 1
        while (response != null) {
            response = response.priorResponse()
            result++
        }
        return result
    }

    fun getApiService(okHttpClient: OkHttpClient = getBasicAuthClient("", "", false))
            : AcademyApi {
        if (api == null) {
            api = getRetrofit(okHttpClient).create(AcademyApi::class.java)
        }
        return api!!
    }

    private fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        if (gson == null) {
            gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
        }
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.SERVER_URL)
                    //need for interceptor
                    .client(okHttpClient)
                    .addConverterFactory(DataConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson!!))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
        }
        return retrofit!!
    }

    fun isServerError(code: Int): Int {
        return when (code) {
            400 -> R.string.validation_error
            401 -> R.string.not_authorized
            500 -> R.string.internal_server_error
            else -> {
                R.string.unknown_error
            }
        }
    }

    fun convertTimestamp(startDate: Date): String {
        val currentDate = Calendar.getInstance().time
        val difference = currentDate.time - startDate.time
        return if (difference <= (60 * 60 * 24 * 1000)) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(startDate)
        } else {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(startDate)
        }
    }

    fun changeUserRuntime(email: String, password: String) {
        userSelectionInterceptor.email = email
        userSelectionInterceptor.password = password
    }

    class UserSelectionInterceptor : Interceptor {

        var email: String = ""
        var password: String = ""

        override fun intercept(chain: Interceptor.Chain): Response {
            val credentials = Credentials.basic(email, password)
            val request = chain.request()
            val authenticatedRequest: Request = request.newBuilder()
                    .header("Authorization", credentials)
                    .build()
            return chain.proceed(authenticatedRequest)
        }
    }
}

