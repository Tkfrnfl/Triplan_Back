package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.user.Role
import com.Triplan.Triplan.domain.user.User
import com.Triplan.Triplan.domain.user.dto.login.LoginResponseDto
import com.Triplan.Triplan.jwt.JwtTokenProvider
import com.Triplan.Triplan.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val authService: AuthService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    fun kakaoSignup(code: String): LoginResponseDto {
        val accessToken = authService.getKakaoAccessTokenByCode(code)
        val kakaoUser = authService.saveUserInfoByKakaoToken(accessToken)
        val existUser = userRepository.findByRoleAndSocialId(Role.KAKAO, kakaoUser.get().socialId.toString())

        return if (existUser.isPresent) {
            login(existUser.get())
        } else {
            userRepository.save(kakaoUser.get())
            login(kakaoUser.get())
        }
    }

    fun login(user: User): LoginResponseDto {
        val refreshToken = jwtTokenProvider.createRefreshToken(user.id)
        authService.updateRefreshToken(user.id, refreshToken)
        user.modifiedDate = LocalDateTime.now()

        return LoginResponseDto.from(
            jwtTokenProvider.createAccessToken(user.id),
            refreshToken
        )
    }

    fun findById(userId: Long): User {
        return userRepository.findById(userId).get()
    }
}