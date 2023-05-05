package com.Triplan.Triplan.exception

import org.springframework.http.HttpStatus

enum class ExceptionCode(val status: HttpStatus, val value: String) {
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "INVALID INPUT VALUE"),
    DUPLICATED_USER(HttpStatus.BAD_REQUEST, "DUPLICATED USER"),
    SOCIAL_LOGIN_FAIL(HttpStatus.BAD_REQUEST, "SOCIAL LOGIN FAIL"),

    WRONG_PASSWORD(HttpStatus.UNAUTHORIZED, "WRONG PASSWORD"),
    FAIL_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "FAIL AUTHENTICATION"), // 로그인X 유저의 요청
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN EXPIRED"), // 엑세스 토큰 만료
    FAIL_AUTHORIZATION(HttpStatus.FORBIDDEN, "FAIL AUTHORIZATION"), // 권한 없는 요청
    JWT_EXCEPTION(HttpStatus.FORBIDDEN, "JWT EXCEPTION"), // 토큰 문제 (JWT)

    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT FOUND"),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR");
}