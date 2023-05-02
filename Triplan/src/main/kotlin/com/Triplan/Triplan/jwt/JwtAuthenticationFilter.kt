package com.Triplan.Triplan.jwt

import com.Triplan.Triplan.exception.ExceptionCode
import com.Triplan.Triplan.exception.ExceptionResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {

    companion object {
        const val AUTHORIZATION = "Authorization"
        const val PREFIX_BEARER = "Bearer "
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val accessToken = resolveToken(request)
        val errorCode = jwtTokenProvider.validateToken(accessToken)

        if (accessToken != null && errorCode == null) {
            try {
                val userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getJwtTokenPayload(accessToken).toString())
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    userDetails.username.toLong(),
                    "",
                    userDetails.authorities
                )
            } catch (e: Exception) {
                response.status = HttpStatus.UNAUTHORIZED.value()
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                objectMapper.writeValue(
                    response.outputStream,
                    ExceptionResponse.of(ExceptionCode.FAIL_AUTHENTICATION)
                )
                return
            }
        }

        request.setAttribute("exception", errorCode)
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION)
        if (bearerToken.isNotEmpty() && bearerToken.startsWith(PREFIX_BEARER)) {
            return bearerToken.substring(7)
        }
        return null
    }
}