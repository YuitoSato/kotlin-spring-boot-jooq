package com.example.kotlinspringbootjooq.domain

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.andThen
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.map
import com.github.michaelbull.result.zip
import com.github.michaelbull.result.zipOrAccumulate

class User(
  val id: UserId,
  val userName: UserName,
  val email: Email,
) {

  companion object {
    fun validateAndCreate(
      userName: String,
      email: String,
    ): Result<User, ValidateAndCreateUserError> {
      return UserName.validateAndCreate(userName)
        .andThen { validatedUserName ->
          Email.validateAndCreate(email)
            .map { validatedEmail ->
              User(
                id = UserId.generate(),
                userName = validatedUserName,
                email = validatedEmail,
              )
            }
        }
    }

    fun validateAndCreate_binding(
      userName: String,
      email: String,
    ): Result<User, ValidateAndCreateUserError> = binding {
      val validatedUserName = UserName.validateAndCreate(userName).bind()
      val validatedEmail = Email.validateAndCreate(email).bind()
      User(
        id = UserId.generate(),
        userName = validatedUserName,
        email = validatedEmail,
      )
    }

    fun validateAndCreate_zip(
      userName: String,
      email: String,
    ): Result<User, ValidateAndCreateUserError> {
      return zip(
        { UserName.validateAndCreate(userName) },
        { Email.validateAndCreate(email) },
      ) { validatedUserName, validatedEmail ->
        User(
          id = UserId.generate(),
          userName = validatedUserName,
          email = validatedEmail,
        )
      }
    }

    fun validateAndCreate_zipOrAccumulate(
      userName: String,
      email: String,
    ): Result<User, List<ValidateAndCreateUserError>> {
      // https://github.com/michaelbull/kotlin-result/issues/89
      return zipOrAccumulate(
        { UserName.validateAndCreate(userName) },
        { Email.validateAndCreate(email) },
      ) { validatedUserName, validatedEmail ->
        User(
          id = UserId.generate(),
          userName = validatedUserName,
          email = validatedEmail,
        )
      }
    }
  }
}

sealed interface ValidateAndCreateUserError {
  data class UserNameInvalidLength(val name: String) : ValidateAndCreateUserError

  // ...
  data class EmailInvalidFormat(val email: String) : ValidateAndCreateUserError
  // ...
}


class UserName(val value: String) {

  companion object {
    fun validateAndCreate(
      name: String,
    ): Result<UserName, ValidateAndCreateUserError> {
      return TODO()
    }
  }
}

class UserId(val value: String) {

  companion object {
    fun generate(): UserId {
      return TODO()
    }
  }
}

class Email(val value: String) {

  companion object {
    fun validateAndCreate(
      email: String,
    ): Result<Email, ValidateAndCreateUserError> {
      return TODO()
    }
  }
}
