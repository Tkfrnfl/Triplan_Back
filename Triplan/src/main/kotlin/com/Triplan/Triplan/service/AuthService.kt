package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.user.User
import com.Triplan.Triplan.repository.UserRepository
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.Optional

@Service
class AuthService(
    private val userRepository: UserRepository,
) {

    fun saveUserInfoByKakaoToken(accessToken: String): Optional<User> {
        try {
            val headers = HttpHeaders()
            headers.add("Authorization", "$accessToken")
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
            val rt = RestTemplate()
            rt.requestFactory = HttpComponentsClientHttpRequestFactory()

            val kakaoProfileRequest = HttpEntity<MultiValueMap<String, String>>(headers)

            val response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                kakaoProfileRequest,
                String::class.java
            )

            val parser = JSONParser()
            val elem = parser.parse(response.body) as JSONObject

            val id = (elem["id"] as Long).toString()
            val email = (elem["kakao_account"] as JSONObject)["email"] as String
            val nickname = (elem["properties"] as JSONObject)["nickname"] as String
            val img = (elem["properties"] as JSONObject)["profile_image"] as String

            val user = Optional.of(User.createKakaoUser(id, email, img, nickname))

            return user
        } catch (exception: Exception) {
            throw Exception("user is not authenticated")
        }
    }

    fun findEmailByKakaoToken(accessToken: String): String {
        try {
            val headers = HttpHeaders()
            headers.add("Authorization", "$accessToken")
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
            val rt = RestTemplate()
            rt.requestFactory = HttpComponentsClientHttpRequestFactory()

            val kakaoProfileRequest = HttpEntity<MultiValueMap<String, String>>(headers)

            val response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                kakaoProfileRequest,
                String::class.java
            )

            val parser = JSONParser()
            val elem = parser.parse(response.body) as JSONObject

            return (elem["kakao_account"] as JSONObject)["email"] as String

        } catch (exception: Exception) {
            throw Exception("user is not authenticated")
        }
    }
}