package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.plan.DayPlan
import com.Triplan.Triplan.domain.plan.Plan
import com.Triplan.Triplan.domain.plan.TripPlace
import com.Triplan.Triplan.domain.plan.dto.request.DayPlanRequestDto
import com.Triplan.Triplan.domain.plan.dto.request.PlanRequestDto
import com.Triplan.Triplan.domain.user.User
import com.Triplan.Triplan.repository.PlanRepository
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import kotlin.Exception

@Service
@PropertySource("classpath:env.properties")
class PlanService(
    @Value("\${auth.naver.X-KEY-ID}")
    val naverKeyID: String,
    @Value("\${auth.naver.X-KEY}")
    val naverKey: String,
    private var planRepository: PlanRepository
) {
    fun findByUser(user: User): Plan {

        return planRepository.findByUser(user).get()
    }

    fun route(request: PlanRequestDto, requests: List<DayPlanRequestDto>): Plan {
        try {
            val result = Plan()
            result.startDate = request.startDate
            result.endDate = request.endDate
            result.touristArea = request.touristArea
            println(requests[0].startingPoint)
            println(requests[0].tripPlaces)
            //일자별 계획 계산
            for ((idx, day) in requests.withIndex()) {
//                println("${day.startingPoint}  ${day.destination}  ${day.tripPlaces}")
                val dayPlan = DayPlan()
                val start = day.startingPoint?.split(", ")?.reversed()?.joinToString()
                val destination = day.destination?.split(", ")?.reversed()?.joinToString()
                val routes = mutableListOf<HashMap<String, String>>()
                println(start)
                /*// Ex){place1 : {'x': '127.0315025', 'y': '37.4909898'}}
                val coordinates = HashMap<String, HashMap<String, String>>()

                // 장소별 좌표 api받기
                for (place in day.tripPlaces){
                    val headers = HttpHeaders()
                    headers.add("X-NCP-APIGW-API-KEY-ID", naverKeyID)
                    headers.add("X-NCP-APIGW-API-KEY",naverKey)
                    val url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=${place}"
                    val rt = RestTemplate()
                    rt.requestFactory = HttpComponentsClientHttpRequestFactory()
                    val geoCodeRequest = HttpEntity<MultiValueMap<String, String>>(headers)

                    val response = rt.exchange(
                        url,
                        HttpMethod.GET,
                        geoCodeRequest,
                        String::class.java
                    )

                    val parser = JSONParser()
                    val elem = parser.parse(response.body) as JSONObject
                    val address = (elem["addresses"] as JSONArray)[0] as JSONObject

                    coordinates[place] = HashMap(
                        mapOf("x" to address["x"] as String,
                            "y" to address["y"] as String
                        )
                    )

                    println("$place  ${coordinates[place]}")
                }*/

                // 중간 경유지 순열 생성
                val permutationResult = permutation(day.tripPlaces.slice(1 until day.tripPlaces.size - 1), listOf())

                for (stopover in permutationResult) {

                    // 네이버 길찾기 api
                    val headers = HttpHeaders()
                    var waypoint = ""
                    println(stopover)
                    stopover.forEach {
                        if (waypoint != ""){
                            waypoint += "|"
                        }
                        waypoint += it.split(", ").reversed().joinToString(",")
                    }
                    println(waypoint)
                    headers.add("X-NCP-APIGW-API-KEY-ID", naverKeyID)
                    headers.add("X-NCP-APIGW-API-KEY", naverKey)
                    val requestParams = "?start=${start}&goal=${destination}&waypoints=${waypoint}"
                    val url = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving$requestParams"
                    val rt = RestTemplate()
                    rt.requestFactory = HttpComponentsClientHttpRequestFactory()
                    val geoCodeRequest = HttpEntity<MultiValueMap<String, String>>(headers)

                    val response = rt.exchange(
                        url,
                        HttpMethod.GET,
                        geoCodeRequest,
                        String::class.java
                    )
                    println(response)
                            println(response.statusCode)


                    val parser = JSONParser()
                    var elem = parser.parse(response.body) as JSONObject

                    if(elem["code"].toString()=="0"){
                        println(elem["code"])
                        val parsedResult =
                                (((elem["route"] as JSONObject)["traoptimal"] as JSONArray)[0] as JSONObject)["summary"] as JSONObject

                        routes.add(hashMapOf(
                                "stopover" to waypoint,
                                "duration" to parsedResult["duration"].toString(),
                                "distance" to parsedResult["distance"].toString()
                        ))
                    }
                    else{

                        elem=adjustLocation(stopover,start,destination)
                        if(elem["code"]==null){
                            return result
                        }
                        val parsedResult =
                                (((elem["route"] as JSONObject)["traoptimal"] as JSONArray)[0] as JSONObject)["summary"] as JSONObject

                        routes.add(hashMapOf(
                                "stopover" to waypoint,
                                "duration" to parsedResult["duration"].toString(),
                                "distance" to parsedResult["distance"].toString()
                        ))
                    }

                }


//                println("${idx+1}일차 최소시간 : ${routes.minBy { (it["duration"] as String).toInt()}}")
//                println("${idx+1}일차 최단거리 : ${routes.minBy { (it["distance"] as String).toInt()}}")

                val minTimeRoute = routes.minBy { (it["duration"] as String).toInt()}["stopover"]
                    ?.split("|")!!.toList()

                dayPlan.tripPlaces.add(TripPlace().apply { location=start })
                minTimeRoute.forEach{
                    dayPlan.tripPlaces.add(TripPlace().apply{location=it})
                }
                dayPlan.tripPlaces.add(TripPlace().apply {location=destination})

                result.dayPlans.add(dayPlan)
            }

            return result
        } catch (exception: Exception) {
            println(exception)
            throw Exception("Plan Route Exception")
        }
    }


    fun <T> permutation(el: List<T>, fin: List<T> = listOf(), sub: List<T> = el): List<List<T>> {
        return if (sub.isEmpty()) listOf(fin)
        else sub.flatMap { permutation(el, fin + it, sub - it) }
    }

    // 경유지가 도로주변이 아닐때 위치보정시도, 그래도 안될시 null
    fun adjustLocation(stopover:List<String>,start:String?,destination:String?):JSONObject{
        val tempList:MutableList<String> = stopover.toMutableList()
        var result=JSONObject()

        for (i in 0 until stopover.size){

            val headers = HttpHeaders()
            var waypoint = ""
           // println((tempList[i].toInt()+0.005).toString())
            var tempSplitList=tempList[i].split(",")
            tempList[i]=String.format("%.7f",tempSplitList[0].toFloat()+0.01)+","+String.format("%.7f",tempSplitList[1].toFloat()+0.005)
            println(tempList)
            tempList.forEach {
                if (waypoint != ""){
                    waypoint += "|"
                }
                waypoint += it.split(", ").reversed().joinToString(",")
            }
            println("check")
            println(waypoint)
            headers.add("X-NCP-APIGW-API-KEY-ID", naverKeyID)
            headers.add("X-NCP-APIGW-API-KEY", naverKey)
            val requestParams = "?start=${start}&goal=${destination}&waypoints=${waypoint}"
            val url = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving$requestParams"
            val rt = RestTemplate()
            rt.requestFactory = HttpComponentsClientHttpRequestFactory()
            val geoCodeRequest = HttpEntity<MultiValueMap<String, String>>(headers)

            val response = rt.exchange(
                    url,
                    HttpMethod.GET,
                    geoCodeRequest,
                    String::class.java
            )


            val parser = JSONParser()
            var elem = parser.parse(response.body) as JSONObject
            println(response)
            if(elem["code"].toString()=="0"){
                result=elem
                break
            }
        }
        return result
    }
}