package com.Triplan.Triplan.domain.user.dto.member

import com.Triplan.Triplan.domain.user.User
import java.time.format.DateTimeFormatter

class UserInfoDto(user: User) {

    val email: String? = user.email

    val nickname: String? = user.nickname

    val signupDate: String? = user.createdDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val loginDate: String? = user.modifiedDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    val userState: String? = user.userState?.value


}