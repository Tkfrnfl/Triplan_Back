package com.Triplan.Triplan.service

import com.Triplan.Triplan.jwt.JwtTokenProvider
import com.Triplan.Triplan.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
}