package com.example.kotlinspringbootjooq.application

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
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
}
