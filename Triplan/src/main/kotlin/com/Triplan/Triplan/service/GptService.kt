package com.Triplan.Triplan.service

import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Transactional
@Service
@PropertySource("classpath:env.properties")


class GptService (@Value("\${auth.gpt.key}")
                  val gptKey:String){
    //jwt 필터로 유저인증되는것인지?
    fun getQuestion(question:String){

        try{
            val rt = RestTemplate()
            rt.requestFactory = HttpComponentsClientHttpRequestFactory()

            val headers = HttpHeaders()
            headers.setContentType(MediaType.APPLICATION_JSON)
            headers.set("Authorization", "Bearer " + gptKey);



            val jsonObjectMessage=JSONObject()
            jsonObjectMessage.put("role","user")
            jsonObjectMessage.put("content",question+" 장소 나열")

            val messages=JSONArray()
            messages.add(jsonObjectMessage)


            val jsonObject =  JSONObject()
            jsonObject.put("model", "gpt-3.5-turbo")
            jsonObject.put("messages", messages)
            jsonObject.put("max_tokens", 2000)
            jsonObject.put("temperature", 1.0)


            val entity: HttpEntity<String> = HttpEntity<String>(jsonObject.toString(), headers)

            val response = rt.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String::class.java
            )
            println(response.body.toString())
            return
        }catch (exception: Exception) {
            println(exception.toString())
        }
    }
}