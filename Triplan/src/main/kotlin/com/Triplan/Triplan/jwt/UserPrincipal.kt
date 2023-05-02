package com.Triplan.Triplan.jwt

import com.Triplan.Triplan.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.Collections

class UserPrincipal(var userId: Long, var authorities: MutableCollection<out GrantedAuthority>): UserDetails {

    companion object {
        val TAG = "UserPrincipal"
        fun create(user: User): UserPrincipal {
            var authorities = Collections.singletonList(SimpleGrantedAuthority(user.role?.value))
            return UserPrincipal(user.id, authorities)
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return userId.toString()
    }

    override fun isAccountNonExpired(): Boolean {
        return false
    }

    override fun isAccountNonLocked(): Boolean {
        return false
    }

    override fun isCredentialsNonExpired(): Boolean {
        return false
    }

    override fun isEnabled(): Boolean {
        return false
    }
}