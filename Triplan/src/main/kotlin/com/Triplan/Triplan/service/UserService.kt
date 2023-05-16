package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.user.Role
import com.Triplan.Triplan.domain.user.dto.member.UserInfoDto
import com.Triplan.Triplan.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val authService: AuthService
) {

    fun login(accessToken: String) {
        val user = authService.saveUserInfoByKakaoToken(accessToken).get()

        if (!userRepository.findByRoleAndSocialId(Role.KAKAO, user.socialId).isPresent) {
            userRepository.save(user)
        }
    }

    fun userInfo(userId: String?): UserInfoDto {

        return UserInfoDto(userRepository.findByRoleAndSocialId(Role.KAKAO, userId).get())
    }

    fun findSocialId(accessToken: String): String? {

        return authService.saveUserInfoByKakaoToken(accessToken).get().socialId
    }
}