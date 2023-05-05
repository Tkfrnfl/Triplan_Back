package com.Triplan.Triplan.jwt

import com.Triplan.Triplan.exception.ExceptionCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.util.Date

@Component
@PropertySource("classpath:env.properties")
class JwtTokenProvider(
    @Value("\${auth.jwtSecret}") var jwtSecret: String,
    @Value("\${auth.jwtExpiration.accessToken}") var jwtAccessToken: Int,
    @Value("\${auth.jwtExpiration.refreshToken}") var jwtRefreshToken: Int) {

    private fun createToken(payload: Long, jwtSecret: String, jwtExpirationAccessToken: Int): String {

        return Jwts.builder().setSubject(payload.toString()).signWith(SignatureAlgorithm.HS256, jwtSecret)
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationAccessToken)).compact()
    }

    fun createAccessToken(payload: Long): String {
        return createToken(payload, jwtSecret, jwtAccessToken)
    }

    fun createRefreshToken(payload: Long): String {
        return createToken(payload, jwtSecret, jwtRefreshToken)
    }

    fun getJwtTokenPayload(token: String): Long {
        try {
            val claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body

            return claims.subject.toLong()
        } catch(exception: ExpiredJwtException) {
            throw Exception()
        } catch(exception: MalformedJwtException) {
            throw Exception()
        } catch(exception: UnsupportedJwtException) {
            throw Exception()
        } catch(exception: IllegalArgumentException) {
            throw Exception()
        }
    }

    fun validateToken(token: String?): ExceptionCode? {
        try {
            val claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
            if (claims.body.expiration.before(Date())) {
                return ExceptionCode.FAIL_AUTHENTICATION
            }
            return null
        } catch (exception: ExpiredJwtException) {
            return ExceptionCode.TOKEN_EXPIRED
        } catch (exception: Exception) { 
            return ExceptionCode.FAIL_AUTHENTICATION
        }
    }
}