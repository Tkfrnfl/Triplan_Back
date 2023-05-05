package com.Triplan.Triplan.domain.user.dto.login

class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String
) {

    companion object {
        fun from(accessToken: String, refreshToken: String): LoginResponseDto {
            return LoginResponseDto(accessToken, refreshToken)
        }
    }
}