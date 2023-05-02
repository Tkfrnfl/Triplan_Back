package com.Triplan.Triplan.domain.user

import com.Triplan.Triplan.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
class User(socialId: String, email: String, img: String, nickname: String, role: Role): BaseTimeEntity() {

    @Id
    @GeneratedValue
    var id: Long = 0L;

    var socialId: String? = null;

    var email: String? = null;

    var img: String? = null;

    var nickname: String? = null;

    @Enumerated(EnumType.STRING)
    var role: Role? = null;

    @Enumerated(EnumType.STRING)
    var userState: UserState? = null;

    var refreshToken: String? = null;

    companion object {
        fun createKakaoUser(kakaoId: String, email: String, img: String, nickname: String): User {
            return User(kakaoId, email, img, nickname, Role.KAKAO);
        }

        fun createNaverUser(naverId: String, email: String, img: String, nickname: String): User {
            return User(naverId, email, img, nickname, Role.NAVER);
        }
    }
}