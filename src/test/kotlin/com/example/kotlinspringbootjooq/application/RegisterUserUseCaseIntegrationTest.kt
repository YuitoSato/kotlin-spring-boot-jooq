package com.example.kotlinspringbootjooq.application

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.test.assertTrue

@SpringBootTest
class RegisterUserUseCaseIntegrationTest(
    @Autowired
    private val registerUserUseCase: RegisterUserUseCase
) {

    @Test
    fun shouldRegisterUser() {
        // ユーザ登録
        val actual = registerUserUseCase.execute(
            RegisterUserDto(
                userName = "test-user",
                email = "a@example.com",
            )
        )

        assertTrue(actual.isOk)
    }

    @Test
    @Rollback(false)
    fun shouldRollbackWhenFail() {
        // ユーザ登録
        val actual = registerUserUseCase.executeAndFail(
            RegisterUserDto(
                userName = "test-user",
                email = "a@example.com",
            )
        )

        assertTrue(actual.isErr)
    }
}
