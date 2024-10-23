package com.example.kotlinspringbootjooq.application

import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.SendCreateUserNotificationEmailUseCaseError
import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.ValidateAndCreateUserUseCaseError
import com.example.kotlinspringbootjooq.domain.User
import com.example.kotlinspringbootjooq.domain.UserRepository
import com.example.kotlinspringbootjooq.domain.ValidateAndCreateUserError
import com.example.kotlinspringbootjooq.utils.ResultTransactional
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.mapError
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
@ResultTransactional
class RegisterUserUseCase2(
  private val userRepository: UserRepository,
  private val createUserNotificationEmailSender: CreateUserNotificationEmailSender,
) {

  fun execute(
    param: RegisterUserDto,
  ) {
    executeInner(param)
      .getOrThrow { it.toException() }
  }

  private fun executeInner(
    param: RegisterUserDto,
  ): Result<Unit, RegisterUserUseCaseError> {
    return User.validateAndCreate(
      param.userName,
      param.email,
    ).mapError { error ->
      ValidateAndCreateUserUseCaseError(error)
    }.andThen { createdUser ->
      userRepository.insert(createdUser)
      createUserNotificationEmailSender
        .send(createdUser)
        .mapError { error ->
          SendCreateUserNotificationEmailUseCaseError(error)
        }
    }
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
