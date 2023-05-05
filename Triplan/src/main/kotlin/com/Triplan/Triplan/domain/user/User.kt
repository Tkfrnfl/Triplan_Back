package com.Triplan.Triplan.domain.user

import com.Triplan.Triplan.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(
    name = "Users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["socialId", "role"])
    ]
)
class User(


    var socialId: String? = null,

    var email: String? = null,

    var img: String? = null,

    var nickname: String? = null,

    @Enumerated(EnumType.STRING)
    var role: Role? = null,

    var refreshToken: String? = null,

    var userState: UserState? = null
): BaseTimeEntity() {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    var id: Long = 0L

    companion object {
        fun createKakaoUser(kakaoId: String, email: String, img: String, nickname: String): User {
            return User(kakaoId, email, img, nickname, Role.KAKAO);
        }

        fun createNaverUser(naverId: String, email: String, img: String, nickname: String): User {
            return User(naverId, email, img, nickname, Role.NAVER);
        }
    }
}