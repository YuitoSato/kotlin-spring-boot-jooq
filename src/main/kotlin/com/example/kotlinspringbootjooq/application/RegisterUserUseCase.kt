package com.example.kotlinspringbootjooq.application

import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.SendCreateUserNotificationEmailUseCaseError
import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.ValidateAndCreateUserUseCaseError
import com.example.kotlinspringbootjooq.domain.User
import com.example.kotlinspringbootjooq.domain.UserRepository
import com.example.kotlinspringbootjooq.domain.ValidateAndCreateUserError
import com.example.kotlinspringbootjooq.utils.ResultTransactional
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.mapError
import org.springframework.stereotype.Service

@Service
@ResultTransactional
class RegisterUserUseCase(
  private val userRepository: UserRepository,
  private val createUserNotificationEmailSender: CreateUserNotificationEmailSender,
) {

  fun execute(
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

data class RegisterUserDto(
  val userName: String,
  val email: String,
)

sealed interface RegisterUserUseCaseError {
  data class ValidateAndCreateUserUseCaseError(
    val error: ValidateAndCreateUserError
  ) : RegisterUserUseCaseError

  data class SendCreateUserNotificationEmailUseCaseError(
    val error: SendCreateUserNotificationEmailError
  ) : RegisterUserUseCaseError
}
