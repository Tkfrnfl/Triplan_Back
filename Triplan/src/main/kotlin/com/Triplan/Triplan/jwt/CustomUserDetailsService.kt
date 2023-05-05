package com.Triplan.Triplan.jwt

import com.Triplan.Triplan.domain.user.User
import com.Triplan.Triplan.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val findUser: User = userRepository.findById(username.toLong()).orElseThrow()
        return UserPrincipal.create(findUser)
    }
}