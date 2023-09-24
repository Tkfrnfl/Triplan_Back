package com.Triplan.Triplan.service

import com.Triplan.Triplan.domain.user.Role
import com.Triplan.Triplan.domain.user.User
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

    fun login(accessToken: String):String {
        val user = authService.saveUserInfoByKakaoToken(accessToken).get()

        if (!userRepository.findByRoleAndSocialId(Role.KAKAO, user.socialId).isPresent) {
            userRepository.save(user)
        }
        val findUser: User =userRepository.findByRoleAndSocialId(Role.KAKAO,user.socialId).get()

        return findUser.id.toString()

    }

    fun userInfo(userId: String?): UserInfoDto {

        return UserInfoDto(userRepository.findByRoleAndSocialId(Role.KAKAO, userId).get())
    }

    fun findSocialId(accessToken: String): String? {

        return authService.saveUserInfoByKakaoToken(accessToken).get().socialId
    }

    fun deleteAccount(accessToken: String):String {

        userRepository.deleteUserByEmail(authService.findEmailByKakaoToken(accessToken))

        return "ok"
    }
}