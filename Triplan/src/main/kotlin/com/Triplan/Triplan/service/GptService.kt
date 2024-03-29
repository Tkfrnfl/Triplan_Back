package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.plan.dto.request.DayPlanRequestDto
import com.Triplan.Triplan.domain.plan.dto.request.PlanRequestDto
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import kotlinx.datetime.toKotlinLocalDate
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
    fun getQuestion(question:String, planService:PlanService):ArrayList<String>{

        try {
            val rt = RestTemplate()
            rt.requestFactory = HttpComponentsClientHttpRequestFactory()

            val headers = HttpHeaders()
            headers.setContentType(MediaType.APPLICATION_JSON)
            headers.set("Authorization", "Bearer " + gptKey);


            val jsonObjectMessage = JSONObject()
            jsonObjectMessage.put("role", "user")
            jsonObjectMessage.put("content", question + " 장소만 나열")

            val messages = JSONArray()
            messages.add(jsonObjectMessage)


            val jsonObject = JSONObject()
            jsonObject.put("model", "gpt-3.5-turbo")
            jsonObject.put("messages", messages)
            jsonObject.put("max_tokens", 150)
            jsonObject.put("temperature", 1.0)


            val entity: HttpEntity<String> = HttpEntity<String>(jsonObject.toString(), headers)

            val response = rt.exchange(
                    "https://api.openai.com/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String::class.java
            )
            val parser = JSONParser()

            val elem = parser.parse(response.body) as JSONObject

            val choices = (elem["choices"] as List<JSONObject>)
            val message = ((choices[0])["message"] as JSONObject)["content"] //gpt로부터 응답받은 값
            // println(message.toString())
            var parseByDay: List<String>
            if (message.toString().contains("Day")) {
                parseByDay = message.toString().split("Day")
            } else {
                parseByDay = message.toString().split("일차")
            }
            var parseByN = ArrayList<String>()
            var tmpParse: List<String>
            //var nounList:List<String>

            for (i in 0 until parseByDay.count()) {
                tmpParse = parseByDay[i].split("-")
                if (tmpParse.size < 2) {
                    tmpParse = parseByDay[i].split(":")
                }
                if (tmpParse.size < 2) {
                    tmpParse = parseByDay[i].split("\\n")
                }
                for (j in 0 until tmpParse.count()) {
                    var morning: String = "오전"
                    var launch: String = "점심"
                    var afternoon: String = "오후"
                    tmpParse[j].replace(morning, "")
                    tmpParse[j].replace(launch, "")
                    tmpParse[j].replace(afternoon, "")
                    parseByN.add(tmpParse[j])
                }
            }
            println(parseByN)

            val geoList = ArrayList<String>()
            // val locationMap=HashMap<String,String>()
            //일단 하루 플랜을 기준으로 계산         geo coder로 좌표변환하는 부분
            for (i in 0 until parseByN.size) {
                println(parseByN.size)
                val context = GeoApiContext.Builder()
                        .apiKey("AIzaSyA8eyEBHHJ5QTOuXBsjHl3oyRyxnpKK5wg")
                        .build()
                val results = GeocodingApi.geocode(context, parseByN[i]).await()
                val gson = GsonBuilder().setPrettyPrinting().create()
                if (results.size > 0) {
                    geoList.add(gson.toJson(results[0].geometry.location))
                    //locationMap.put(parseByN[i],results[0].geometry.location.toString())
                    println(gson.toJson(results[0].geometry.location))
                }
            }

            //값 매핑 체크
//            for ((key, value) in locationMap) {
//                println("전체 : ${key} : ${value}")
//            }

            //planRequestDto 형식에 맞게 형변환
            var tmpToday = java.time.LocalDate.now()
            var tempStartDate = java.time.LocalDate.of(tmpToday.year, tmpToday.month, tmpToday.dayOfMonth);
            var tempEndDate = java.time.LocalDate.of(tmpToday.year, tmpToday.month, tmpToday.dayOfMonth + 1);


            var request = PlanRequestDto()
            request.startDate = tempStartDate.toKotlinLocalDate()
            request.endDate = tempEndDate.toKotlinLocalDate()
            request.touristArea = ""

            var requests = ArrayList<DayPlanRequestDto>()
            var tripList = ArrayList<String>()
            // for(i in 1 until geoList.size){
            var dayPlanRequestDto = DayPlanRequestDto()
            val jsonParser = JsonParser()
            var json = jsonParser.parse(geoList[0]).asJsonObject
            println(json["lat"])
            dayPlanRequestDto.startingPoint = json["lat"].toString() + ", " + json["lng"].toString()

            for (i in 1 until geoList.size) {
                json = jsonParser.parse(geoList[i]).asJsonObject
                tripList.add(json["lat"].toString() + "," + json["lng"].toString())
            }
            dayPlanRequestDto.tripPlaces = tripList

            json = jsonParser.parse(geoList[geoList.size - 1]).asJsonObject
            dayPlanRequestDto.destination = json["lat"].toString() + ", " + json["lng"].toString()

            requests.add(dayPlanRequestDto)
            // }

//            val plan: Plan = planService.route(request,requests)      카카오 api로 대체
//            println(plan.dayPlans)


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

            return kakaoRoadApi(requests, parseByN)
        } catch (exception: Exception) {
            println(exception.toString())
            //throw Exception("길찾기 실패")
            var res=ArrayList<String>()
            res.add("길찾기 실패")
            return res
        }
    }

    fun kakaoRoadApi(requests:List<DayPlanRequestDto>,locationString:ArrayList<String>):ArrayList<String>{ // 같은 경유지 고르는경우 예외처리 추가하기
        val headers=HttpHeaders()

        headers.add("Authorization","KakaoAK 2ad875a1d5fbba4e1912c0f930157e89")

        val url= "https://apis-navi.kakaomobility.com/v1/waypoints/directions"
        val rt = RestTemplate()
        rt.requestFactory = HttpComponentsClientHttpRequestFactory()

        val start=JSONObject()
        start.put("x", requests[0].startingPoint?.split(",")?.get(1)?.toDouble())
//        println(requests[0].startingPoint?.split(",")?.get(1)?.toDouble())
//        println(requests[0].startingPoint?.split(",")?.get(0)?.toDouble())
        start.put("y", requests[0].startingPoint?.split(",")?.get(0)?.toDouble())
        val end=JSONObject()
        end.put("x", requests[0].destination?.split(",")?.get(1)?.toDouble())
        end.put("y", requests[0].destination?.split(",")?.get(0)?.toDouble())

        if (start==end){
            println( "도착지와 목적지가같습니다.")
        }
        val waypoints=JSONArray()
        for (i in 0 until requests[0].tripPlaces.size){
            val places=JSONObject()
            places.put("name",locationString[i+1])
            places.put("x",requests[0].tripPlaces[i]?.split(",")?.get(1)?.toDouble())
            places.put("y",requests[0].tripPlaces[i]?.split(",")?.get(0)?.toDouble())

            waypoints.add(places)
        }
        print(waypoints)
        val jsonObjectMessage=JSONObject()
        jsonObjectMessage.put("origin", start)
        jsonObjectMessage.put("destination", end)
        jsonObjectMessage.put("waypoints", waypoints)
        jsonObjectMessage.put("priority", "RECOMMEND")
        jsonObjectMessage.put("car_fuel", "GASOLINE")
        jsonObjectMessage.put("car_hipass", false)
        jsonObjectMessage.put("alternatives", false)
        jsonObjectMessage.put("road_details", false)

        val entity: HttpEntity<JSONObject> = HttpEntity<JSONObject>(jsonObjectMessage, headers)

        val response= rt.exchange(
                url,HttpMethod.POST,entity,String::class.java
        )

        val parser= JSONParser()

        val elem= parser.parse(response.body)as JSONObject
        println(elem[0])
        println(elem["routes"])
        var resValue=((elem["routes"]as List<JSONObject>)[0]["summary"]as JSONObject)["waypoints"] as List<JSONObject>
        println(((elem["routes"]as List<JSONObject>)[0]["summary"]as JSONObject)["waypoints"])

        var res=ArrayList<String>()
        for(i in 0 until resValue.size){
            res.add((resValue[i]as JSONObject)["name"].toString())
        }
        return res
    }


}