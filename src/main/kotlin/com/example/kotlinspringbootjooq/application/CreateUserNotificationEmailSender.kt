package com.example.kotlinspringbootjooq.application

import com.example.kotlinspringbootjooq.domain.User
import com.github.michaelbull.result.Result

interface CreateUserNotificationEmailSender {

  fun send(
    createdUser: User,
  ): Result<Unit, SendCreateUserNotificationEmailError>
}

sealed interface SendCreateUserNotificationEmailError {
  data class RecipientNotFound(val email: String) : SendCreateUserNotificationEmailError
}
