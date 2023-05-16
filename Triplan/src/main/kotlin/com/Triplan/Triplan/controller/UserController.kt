package com.Triplan.Triplan.controller

import com.Triplan.Triplan.domain.user.dto.member.UserInfoDto
import com.Triplan.Triplan.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(private val userService: UserService,
                     private val request: HttpServletRequest) {


    @MutationMapping
    fun login(): ResponseEntity<String> {

        val accessToken = request.getHeader("Authorization")
        userService.login(accessToken)
        return ResponseEntity.ok("signup")
    }

    @QueryMapping
    fun userInfo(): ResponseEntity<UserInfoDto> {

        val accessToken = request.getHeader("Authorization")
        val user = userService.userInfo(userService.findSocialId(accessToken))

        return ResponseEntity.ok(user)
    }
}