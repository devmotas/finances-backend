package com.example.finances.security

import com.example.finances.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(val user: User) : UserDetails {
    val id: Long get() = user.id!!

    override fun getUsername(): String = user.email
    override fun getPassword(): String = user.password
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}
