package com.Triplan.Triplan.jwt

import com.Triplan.Triplan.exception.ExceptionCode
import com.Triplan.Triplan.exception.ExceptionResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
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

@Component
class JwtAuthenticationFilter(@Autowired val userDetailsService: UserDetailsService,
                              @Autowired val jwtTokenProvider: JwtTokenProvider,
                              @Autowired val objectMapper: ObjectMapper): OncePerRequestFilter() {

    companion object {
        val TAG = "JwtAuthenticationFilter"
        val PREFIX_BEARER: String = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var accessToken = resolveToken(request)
        var errorCode: ExceptionCode? = jwtTokenProvider.validateToken(accessToken)

        if (accessToken != null && errorCode == null) {
            try {
                var userDetails: UserDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getJwtTokenPayload(accessToken).toString())
                SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken(userDetails.username.toLong(), "", userDetails.authorities))
            } catch (exception: Exception) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value())
                response.setContentType(MediaType.APPLICATION_JSON_VALUE)
                objectMapper.writeValue(
                    response.outputStream, ExceptionResponse.of(ExceptionCode.FAIL_AUTHENTICATION)
                )
                return
            }
        }

        request.setAttribute("exception", errorCode)
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        var bearerToken: String = request.getHeader(AUTHORIZATION);
        if (bearerToken.isNotEmpty() && bearerToken.startsWith(PREFIX_BEARER)) {
            return bearerToken.substring(7)
        }
        return null
    }
}