package com.Triplan.Triplan.repository

import com.Triplan.Triplan.domain.user.Role
import com.Triplan.Triplan.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository: JpaRepository<User, Long> {

    fun findByRoleAndSocialId(role: Role, socialId: String?): Optional<User>
    fun deleteUserByEmail(email: String?)
}