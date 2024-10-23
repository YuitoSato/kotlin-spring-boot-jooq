package com.example.kotlinspringbootjooq.infrastructure

import com.example.kotlinspringbootjooq.application.CreateUserNotificationEmailSender
import com.example.kotlinspringbootjooq.application.SendCreateUserNotificationEmailError
import com.example.kotlinspringbootjooq.domain.User
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Component

@Component
class CreateUserNotificationEmailSendGridSender() : CreateUserNotificationEmailSender {

  override fun send(
    createdUser: User,
  ): Result<Unit, SendCreateUserNotificationEmailError> {
    // 通知メール送信処理
    return Ok(println(createdUser.email))
  }
}
