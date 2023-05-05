package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.user.User
import com.Triplan.Triplan.domain.user.dto.login.LoginResponseDto
import com.Triplan.Triplan.jwt.JwtTokenProvider
import com.Triplan.Triplan.repository.UserRepository
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.util.Optional

@Service
@PropertySource("classpath:env.properties")
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Value("\${auth.kakao.key}")
    private var authKakaoKey: String? = null

    @Value("\${auth.kakao.redirecturl}")
    private var authKakaoRedirectUrl: String? = null

    fun getKakaoAccessTokenByCode(code: String): String {
        var accessToken = ""
        try {
            val headers = HttpHeaders()
            headers.add("Content-type" ,"application/x-www-form-urlencoded;charset=utf-8")

            val params: MultiValueMap<String, String> = LinkedMultiValueMap()
            params.add("grant_type", "authorization_code")
            params.add("client_id", authKakaoKey)
            params.add("redirect_uri", authKakaoRedirectUrl)
            params.add("code", code)

            val rt = RestTemplate()
            rt.requestFactory = HttpComponentsClientHttpRequestFactory()
            val kakaoTokenRequest = HttpEntity<MultiValueMap<String, String>>(params, headers)
            val response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String::class.java
            )
            val parser = JSONParser()
            val elem = parser.parse(response.body) as JSONObject
            accessToken = elem.get("access_token") as String
        } catch (e: Exception) {
            throw Exception("null in getKakaoAccessTokenByCode")
        }

        return accessToken
    }

    fun saveUserInfoByKakaoToken(accessToken: String): Optional<User> {
        try {
            val headers = HttpHeaders()
            headers.add("Authorization", "Bearer $accessToken")
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
            val rt = RestTemplate()
            rt.requestFactory = HttpComponentsClientHttpRequestFactory()

            val kakaoProfileRequest = HttpEntity<MultiValueMap<String, String>>(headers)

            val response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
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
            throw Exception("what's null")
        }
    }

    fun tokenRefresh(refreshToken: String): LoginResponseDto {
        val isValid = jwtTokenProvider.validateToken(refreshToken) == null

        if (refreshToken.isEmpty() || !isValid) {
            throw Exception()
        }

        val userId = jwtTokenProvider.getJwtTokenPayload(refreshToken)
        val usersRefreshToken = userRepository.findRefreshTokenById(userId)

        if (!refreshToken.equals(usersRefreshToken)) {
            throw Exception()
        }

        val newRefreshToken = jwtTokenProvider.createRefreshToken(userId)
        updateRefreshToken(userId, newRefreshToken)

        return LoginResponseDto.from(
            jwtTokenProvider.createAccessToken(userId),
            newRefreshToken
        )
    }

    fun updateRefreshToken(userId: Long, refreshToken: String) {
        val user = userRepository.findById(userId).orElseThrow()
        user.refreshToken = refreshToken
        userRepository.save(user)
    }
}