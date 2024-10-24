package com.example.kotlinspringbootjooq.application

import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.SendCreateUserNotificationEmailUseCaseError
import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.ValidateAndCreateUserUseCaseError
import com.example.kotlinspringbootjooq.domain.User
import com.example.kotlinspringbootjooq.domain.UserRepository
import com.example.kotlinspringbootjooq.domain.ValidateAndCreateUserError
import com.example.kotlinspringbootjooq.utils.ResultTransactional
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import org.springframework.stereotype.Service

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val createUserNotificationEmailSender: CreateUserNotificationEmailSender,
) {

    @ResultTransactional
    fun execute(
        param: RegisterUserDto,
    ): Result<User, RegisterUserUseCaseError> {
        return User.validateAndCreate(
            param.userName,
            param.email,
        ).mapError { error ->
            ValidateAndCreateUserUseCaseError(error)
        }.andThen { createdUser ->
            userRepository.insert(createdUser)
            createUserNotificationEmailSender
                .send(createdUser)
                .map { createdUser }
                .mapError { error ->
                    SendCreateUserNotificationEmailUseCaseError(error)
                }
        }
    }

    @ResultTransactional
    fun executeAndFail(
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
        }.andThen {
            Err(ValidateAndCreateUserUseCaseError(ValidateAndCreateUserError.UserNameInvalidLength("hoge")))
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
