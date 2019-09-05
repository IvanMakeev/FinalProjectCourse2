package com.example.makeev.myfirstapplication.model.converter

import com.example.makeev.myfirstapplication.model.Data
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class DataConverterFactory : Converter.Factory() {

    //анотация для определения другой фабрики
    @Retention(AnnotationRetention.RUNTIME)
    internal annotation class Json

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        val envelopedType = TypeToken.getParameterized(Data::class.java, type!!).type

        val delegate = retrofit!!.nextResponseBodyConverter<Data<*>>(this, envelopedType, annotations!!)

        //если annotation является Json значит получаем не Data обертку и возвращаем GsonConverterFactory
        for (annotation in annotations) {
            if (annotation is Json)
                return GsonConverterFactory
                        .create(GsonBuilder().excludeFieldsWithoutExposeAnnotation().create())
                        .responseBodyConverter(type, annotations, retrofit)
        }

        return Converter { body: ResponseBody ->
            val data = delegate.convert(body)
            data!!.response
        }
    }
}



