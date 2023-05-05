package com.Triplan.Triplan.controller

import com.Triplan.Triplan.domain.user.dto.login.KakaoLoginRequestDto
import com.Triplan.Triplan.domain.user.dto.login.LoginResponseDto
import com.Triplan.Triplan.domain.user.dto.member.UserInfoDto
import com.Triplan.Triplan.jwt.UserPrincipal
import com.Triplan.Triplan.service.UserService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userService: UserService) {

    @MutationMapping
    fun signup(@RequestBody @Argument request: KakaoLoginRequestDto): LoginResponseDto {
        return userService.kakaoSignup(request.code)
    }

    @QueryMapping
    fun test(): UserInfoDto {
        val userId = SecurityContextHolder.getContext().authentication.principal.toString().toLong()

        return UserInfoDto(userService.findById(userId))
    }
}