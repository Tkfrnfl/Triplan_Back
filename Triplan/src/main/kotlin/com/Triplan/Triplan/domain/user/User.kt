package com.Triplan.Triplan.domain.user

import com.Triplan.Triplan.domain.BaseTimeEntity
import jakarta.persistence.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Entity
@Table(
    name = "Users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["socialId", "role"])
    ]
)
@Serializable(with = KSerializer::class)
class User(


    var socialId: String? = null,

    var email: String? = null,

    var img: String? = null,

    var nickname: String? = null,

    @Enumerated(EnumType.STRING)
    var role: Role? = null,

    var userState: UserState? = null
): BaseTimeEntity() {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    var id: Long = 0L

    @Serializable
    companion object {
        fun createKakaoUser(kakaoId: String, email: String, img: String, nickname: String): User {
            return User(kakaoId, email, img, nickname, Role.KAKAO);
        }
    }
}