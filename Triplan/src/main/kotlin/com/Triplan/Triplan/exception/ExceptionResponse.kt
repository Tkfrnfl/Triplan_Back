package com.Triplan.Triplan.exception

class ExceptionResponse(private var message: String, private var status: Int) {

    constructor(exceptionCode: ExceptionCode): this(exceptionCode.value, exceptionCode.status.value())

    constructor(exceptionCode: ExceptionCode, message: String): this(message, exceptionCode.status.value())

    companion object {
        val TAG = "ExceptionResponse"

        fun of(exceptionCode: ExceptionCode): ExceptionResponse {
            return ExceptionResponse(exceptionCode)
        }

        fun of(exceptionCode: ExceptionCode, additionalMessage: String): ExceptionResponse {
            var message: String = String.format("%s - %s", exceptionCode.value, additionalMessage)
            return ExceptionResponse(exceptionCode, message)
        }
    }
}