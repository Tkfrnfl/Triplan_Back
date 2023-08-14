package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.plan.Plan
import com.Triplan.Triplan.domain.plan.dto.request.DayPlanRequestDto
import com.Triplan.Triplan.domain.plan.dto.request.PlanRequestDto
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Autowired
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
import kotlinx.datetime.LocalDate as LocalDate


@Transactional
@Service
@PropertySource("classpath:env.properties")


class GptService (@Value("\${auth.gpt.key}")
                  val gptKey:String,private val analysisService: AnalysisService){




    //jwt 필터로 유저인증되는것인지?
    fun getQuestion(question:String, planService:PlanService){

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
            val parser= JSONParser()

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
            //var nounList:List<String>

            for(i in 0 until parseByDay.count()){
              tmpParse= parseByDay[i].split("-")
                if (tmpParse.size<2){
                    tmpParse= parseByDay[i].split(":")
                }
                if (tmpParse.size<2){
                    tmpParse= parseByDay[i].split("\\n")
                }
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

            val geoList=ArrayList<String>()

            //일단 하루 플랜을 기준으로 계산
            for (i in 0 until parseByN.size){
                println(parseByN.size)
                val context = GeoApiContext.Builder()
                        .apiKey("AIzaSyA8eyEBHHJ5QTOuXBsjHl3oyRyxnpKK5wg")
                        .build()
                val results = GeocodingApi.geocode(context, parseByN[i]).await()
                val gson = GsonBuilder().setPrettyPrinting().create()
                if(results.size>0){
                    geoList.add(gson.toJson(results[0].geometry.location))
                    println(gson.toJson(results[0].geometry.location))
                }
            }
            var tmpToday=java.time.LocalDate.now()
            var tempStartDate=java.time.LocalDate.of(tmpToday.year,tmpToday.month,tmpToday.dayOfMonth);
            var tempEndDate=java.time.LocalDate.of(tmpToday.year,tmpToday.month,tmpToday.dayOfMonth+1);


            var request=PlanRequestDto()
            request.startDate=tempStartDate.toKotlinLocalDate()
            request.endDate=tempEndDate.toKotlinLocalDate()
            request.touristArea=""

            var requests=ArrayList<DayPlanRequestDto>()
            var tripList=ArrayList<String>()
           // for(i in 1 until geoList.size){
                var dayPlanRequestDto=DayPlanRequestDto()
            val jsonParser = JsonParser()
            var json = jsonParser.parse(geoList[0]).asJsonObject
            println(json["lat"])
                dayPlanRequestDto.startingPoint=json["lat"].toString()+", "+json["lng"].toString()

                for (i in 1 until geoList.size){
                    json = jsonParser.parse(geoList[i]).asJsonObject
                    tripList.add(json["lat"].toString()+","+json["lng"].toString())
                }
                dayPlanRequestDto.tripPlaces=tripList

                json = jsonParser.parse(geoList[geoList.size-1]).asJsonObject
                dayPlanRequestDto.destination=json["lat"].toString()+", "+json["lng"].toString()

                requests.add(dayPlanRequestDto)
           // }
            val plan: Plan = planService.route(request,requests)
            println(plan.dayPlans)




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