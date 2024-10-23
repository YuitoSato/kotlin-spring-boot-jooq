package com.example.kotlinspringbootjooq.presentation

import com.example.kotlinspringbootjooq.application.RegisterUserDto
import com.example.kotlinspringbootjooq.application.RegisterUserUseCase
import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError
import com.example.kotlinspringbootjooq.application.SendCreateUserNotificationEmailError
import com.example.kotlinspringbootjooq.domain.ValidateAndCreateUserError
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.map
import org.apache.coyote.BadRequestException
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
class UserController(
  private val registerUserUseCase: RegisterUserUseCase,
) {

  fun registerUser(
    request: RegisterUserRequest
  ): String {
    val dto = request.toDto()
    val result = registerUserUseCase.execute(dto)
    return result
      .map { "OK" }
      .getOrThrow { error -> error.toException() }
  }
}

data class RegisterUserRequest(
  val userName: String,
  val email: String,
) {

  fun toDto(): RegisterUserDto {
    return RegisterUserDto(
      userName = userName,
      email = email,
    )
  }
}

fun RegisterUserUseCaseError.toException(): BadRequestException {
  return when (this) {
    is RegisterUserUseCaseError.ValidateAndCreateUserUseCaseError -> when (this.error) {
      is ValidateAndCreateUserError.UserNameInvalidLength -> BadRequestException("Invalid user name")
      is ValidateAndCreateUserError.EmailInvalidFormat -> BadRequestException("Invalid email")
    }

    is RegisterUserUseCaseError.SendCreateUserNotificationEmailUseCaseError -> when (this.error) {
      is SendCreateUserNotificationEmailError.RecipientNotFound -> BadRequestException("Recipient not found")
    }
  }
}
