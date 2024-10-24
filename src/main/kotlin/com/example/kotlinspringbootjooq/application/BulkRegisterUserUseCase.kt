package com.example.kotlinspringbootjooq.application

import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.SendCreateUserNotificationEmailUseCaseError
import com.example.kotlinspringbootjooq.application.RegisterUserUseCaseError.ValidateAndCreateUserUseCaseError
import com.example.kotlinspringbootjooq.domain.User
import com.example.kotlinspringbootjooq.domain.UserRepository
import com.example.kotlinspringbootjooq.domain.ValidateAndCreateUserError
import com.example.kotlinspringbootjooq.utils.ResultTransactional
import com.example.kotlinspringbootjooq.utils.combineOrAccumulate
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.combine
import com.github.michaelbull.result.mapError
import org.springframework.stereotype.Service

@Service
@ResultTransactional
class BulkRegisterUserUseCase(
    private val userRepository: UserRepository,
    private val createUserNotificationEmailSender: CreateUserNotificationEmailSender,
) {

    fun execute(
        params: List<RegisterUserDto>,
    ): Result<Unit, RegisterUserUseCaseError> = binding {
        val resultUserList: List<Result<User, ValidateAndCreateUserError>> = params.map { param ->
            User.validateAndCreate(
                param.userName,
                param.email,
            )
        }

        val userListResult2: Result<List<User>, ValidateAndCreateUserUseCaseError> =
            resultUserList.combine().mapError { error ->
                ValidateAndCreateUserUseCaseError(error)
            }

        val userList = userListResult2.bind()

        userRepository.bulkInsert(userList)

        userList.map {
            createUserNotificationEmailSender.send(it)
        }.combine().mapError { error ->
            SendCreateUserNotificationEmailUseCaseError(error)
        }
    }

    fun execute_zip(
        params: List<RegisterUserDto>,
    ): Result<Unit, RegisterUserUseCaseError> = binding {
        val resultUserList: List<Result<User, ValidateAndCreateUserError>> = params.map { param ->
            User.validateAndCreate(
                param.userName,
                param.email,
            )
        }

        val userListResult2: Result<List<User>, ValidateAndCreateUserUseCaseError> =
            resultUserList.combine().mapError { error ->
                ValidateAndCreateUserUseCaseError(error)
            }

        val userList = userListResult2.bind()

        userRepository.bulkInsert(userList)

        userList.map {
            createUserNotificationEmailSender.send(it)
        }.combine().mapError { error ->
            SendCreateUserNotificationEmailUseCaseError(error)
        }
    }

    fun execute_combineOrAccumulate(
        params: List<RegisterUserDto>,
    ): Result<Unit, List<RegisterUserUseCaseError>> = binding {
        val resultUserList: List<Result<User, ValidateAndCreateUserError>> = params.map { param ->
            User.validateAndCreate(
                param.userName,
                param.email,
            )
        }

        val userListResult2: Result<List<User>, List<ValidateAndCreateUserUseCaseError>> =
            resultUserList.combineOrAccumulate().mapError { errors ->
                errors.map { ValidateAndCreateUserUseCaseError(it) }
            }

        val userList = userListResult2.bind()

        userRepository.bulkInsert(userList)

        userList.map {
            createUserNotificationEmailSender.send(it)
        }.combineOrAccumulate().mapError { errors ->
            errors.map { SendCreateUserNotificationEmailUseCaseError(it) }
        }
    }
}
