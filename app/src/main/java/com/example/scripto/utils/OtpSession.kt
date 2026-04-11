package com.example.scripto.utils

object OtpSession {
    var email: String = ""
    var otp: String = ""
    var expireTime: Long = 0
    var attempts: Int = 0
}