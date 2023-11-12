package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.AddressCoordinate
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
import org.springframework.http.ResponseEntity
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
    @Value("\${auth.kakao.key}")
    val kakaoRestApiKey: String,
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

            //일자별 계획 계산
            for ((idx, day) in requests.withIndex()) {
                val dayPlan = DayPlan()

                // 목적지별 좌표 요청
                val dayCoordinates = day.tripPlaces.stream()
                    .map(this::getCoordinate)
                    .toList()


                val start = dayCoordinates.first()
                val destination = dayCoordinates.last()

                val routes = mutableListOf<HashMap<String, String>>()

                // 중간 경유지 순열 생성
                val permutationResult = permutation(dayCoordinates.slice(1 until day.tripPlaces.size - 1), listOf())

                for (permutationStopover in permutationResult) {

                    // 카카오 길찾기 api
                    val response = getRoute(start, destination, permutationStopover)

                    val parser = JSONParser()
                    val elem = (parser.parse(response.body) as JSONObject)["routes"] as JSONArray
                    if (elem.size != 0) {

                        val parsedResult = (elem[0] as JSONObject)["summary"] as JSONObject
                        val duration = parsedResult["duration"] as Long
                        val distance = parsedResult["distance"] as Long

                        routes.add(
                            hashMapOf(
                                "route" to permutationStopover.joinToString(","),
                                "duration" to duration.toString(),
                                "distance" to distance.toString()
                            )
                        )
                    }
                    /*else {

                            elem = adjustLocation(stopover, start, destination)
                            if (elem["code"] == null) {
                                return result
                            }
                            val parsedResult =
                                (((elem["route"] as JSONObject)["traoptimal"] as JSONArray)[0] as JSONObject)["summary"] as JSONObject

                            routes.add(
                                hashMapOf(
                                    "stopover" to waypoint,
                                    "duration" to parsedResult["duration"].toString(),
                                    "distance" to parsedResult["distance"].toString()
                                )
                            )
                        }*/

                }


//                println("${idx+1}일차 최소시간 : ${routes.minBy { (it["duration"] as String).toInt()}}")
//                println("${idx+1}일차 최단거리 : ${routes.minBy { (it["distance"] as String).toInt()}}")

                val minTimeRoute = routes.minBy { (it["duration"] as String).toInt() }["route"]?.split(",")!!.toList()
                println("minTimeRoute = ${minTimeRoute.joinToString()}")

                dayPlan.tripPlaces.add(TripPlace().apply { location = start.name })

                minTimeRoute.forEach {
                    dayPlan.tripPlaces.add(TripPlace().apply { location = it })
                }
                dayPlan.tripPlaces.add(TripPlace().apply { location = destination.name })

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

    private fun getCoordinate(address: String): AddressCoordinate {

        val headers = HttpHeaders()
        headers.add("Authorization", "KakaoAK $kakaoRestApiKey")

        val url = "https://dapi.kakao.com/v2/local/search/address?query=$address"
        val rt = RestTemplate()
        rt.requestFactory = HttpComponentsClientHttpRequestFactory()
        val httpEntity = HttpEntity<MultiValueMap<String, String>>(headers)

        val response = rt.exchange(
            url,
            HttpMethod.GET,
            httpEntity,
            String::class.java
        )

        val parsed: JSONObject
        val parser = JSONParser()
        val parsedBody = (parser.parse(response.body) as JSONObject)["documents"] as JSONArray

        parsed = parsedBody[0] as JSONObject


        return AddressCoordinate(
            address,
            parsed["x"] as String,
            parsed["y"] as String
        )
    }

    private fun getRoute(
        start: AddressCoordinate,
        destination: AddressCoordinate,
        stopover: List<AddressCoordinate>
    ): ResponseEntity<String> {

        // 카카오 길찾기 api
        val headers = HttpHeaders()
        headers.add("Authorization", "KakaoAK $kakaoRestApiKey")
        headers.add("Content-Type", "application/json")


        val url = "https://apis-navi.kakaomobility.com/v1/waypoints/directions"
        val rt = RestTemplate()
        rt.requestFactory = HttpComponentsClientHttpRequestFactory()

        val bodies = JSONObject()
        bodies.put("origin", start.toJson())
        bodies.put("destination", destination.toJson())
        bodies.put(
            "waypoints", stopover.stream()
                .map(AddressCoordinate::toJson)
        )

        val httpEntity = HttpEntity<JSONObject>(bodies, headers)

        val response = rt.exchange(
            url,
            HttpMethod.POST,
            httpEntity,
            String::class.java
        )
        println("response : ${response.body}")

        return response
    }

    // 경유지가 도로주변이 아닐때 위치보정시도, 그래도 안될시 null
    fun adjustLocation(stopover: List<String>, start: String?, destination: String?): JSONObject {
        val tempList: MutableList<String> = stopover.toMutableList()
        var result = JSONObject()

        for (i in 0 until stopover.size) {

            val headers = HttpHeaders()
            var waypoint = ""
            // println((tempList[i].toInt()+0.005).toString())
            var tempSplitList = tempList[i].split(",")
            tempList[i] = String.format("%.7f", tempSplitList[0].toFloat() + 0.01) + "," + String.format(
                "%.7f",
                tempSplitList[1].toFloat() + 0.005
            )
            println(tempList)
            tempList.forEach {
                if (waypoint != "") {
                    waypoint += "|"
                }
                waypoint += it.split(", ").reversed().joinToString(",")
            }

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
            if (elem["code"].toString() == "0") {
                result = elem
                break
            }
        }
        return result
    }
}