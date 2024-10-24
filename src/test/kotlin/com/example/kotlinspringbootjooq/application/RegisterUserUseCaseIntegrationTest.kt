package com.example.kotlinspringbootjooq.application

import com.example.kotlinspringbootjooq.domain.UserRepository
import com.github.michaelbull.result.get
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
class RegisterUserUseCaseIntegrationTest(
    @Autowired
    private val registerUserUseCase: RegisterUserUseCase,
    @Autowired
    private val userRepository: UserRepository,
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

        val user = userRepository.findById(actual.get()!!.id)

        assertEquals("test-user", user!!.userName.value)
        assertEquals("a@example.com", user.email.value)

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
