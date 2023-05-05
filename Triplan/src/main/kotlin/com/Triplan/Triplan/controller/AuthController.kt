package com.Triplan.Triplan.controller

import com.Triplan.Triplan.domain.user.dto.login.LoginResponseDto
import com.Triplan.Triplan.domain.user.dto.login.TokenRefreshRequestDto
import com.Triplan.Triplan.service.AuthService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(private val authService: AuthService) {

    @MutationMapping
    fun tokenRefresh(@RequestBody @Argument request: TokenRefreshRequestDto): LoginResponseDto {
        return authService.tokenRefresh(request.refreshToken)
    }
}