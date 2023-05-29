package com.Triplan.Triplan.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
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
                  val gptKey:String,private val analysisService: AnalysisService){

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
            jsonObjectMessage.put("content",question+" 장소만 나열")

            val messages=JSONArray()
            messages.add(jsonObjectMessage)


            val jsonObject =  JSONObject()
            jsonObject.put("model", "gpt-3.5-turbo")
            jsonObject.put("messages", messages)
            jsonObject.put("max_tokens", 300)
            jsonObject.put("temperature", 1.0)


            val entity: HttpEntity<String> = HttpEntity<String>(jsonObject.toString(), headers)

            val response = rt.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String::class.java
            )
            val parser=JSONParser()

            val elem= parser.parse(response.body)as JSONObject

            val choices=(elem["choices"] as List<JSONObject>)
            val message=((choices[0])["message"] as JSONObject)["content"]
           // println(message.toString())
            var parseByDay:List<String>
            if(message.toString().contains("Day")){
                parseByDay= message.toString().split("Day")
            }
            else{
                parseByDay= message.toString().split("일차")
            }
            var parseByN= ArrayList<String>()
            var tmpParse:List<String>
            var nounList:List<String>

            for(i in 0 until parseByDay.count()){
              tmpParse= parseByDay[i].split("-")
                for(j in 0 until tmpParse.count()){
                    var morning:String="오전"
                    var launch:String="점심"
                    var afternoon:String="오후"
                    tmpParse[j].replace(morning,"")
                    tmpParse[j].replace(launch,"")
                    tmpParse[j].replace(afternoon,"")
                    parseByN.add(tmpParse[j])
                }
            }
            println(parseByN)


//            for(i in 0 until parseByDay.count()){       //일차 별로 파싱
//                tmpParse= parseByDay[i].split("-")
//                for(j in 0 until tmpParse.count()){
//                    parseByN.add(tmpParse[j])
//                }
//                nounList= analysisService.analysisSentence(parseByN)    // 형태소 분석기 실행
//                //받아온 명사들 정보처리 Service로 넘겨서 구글맵 검색기록으로 받아옴,
//                //장소로 나오는 경우 따로 일자별로 정보 저장
//                //println(nounList)
//                parseByN= arrayListOf()
//            }
            // 일자별로 저장한 것을 이용하여 gpt 구문에 정보 표시
            //*  만약 성능이 너무 떨어질시 질문에"장소 별로" 추가 고려

            return
        }catch (exception: Exception) {
            println(exception.toString())
        }
    }
}