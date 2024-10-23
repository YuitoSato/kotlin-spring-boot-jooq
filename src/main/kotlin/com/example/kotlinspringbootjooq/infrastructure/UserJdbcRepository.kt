package com.example.kotlinspringbootjooq.infrastructure

import com.example.kotlinspringbootjooq.domain.User
import com.example.kotlinspringbootjooq.domain.UserRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class UserJdbcRepository(
  val jooq: DSLContext
) : UserRepository {
  override fun insert(user: User) {
    println("save user ${user}")
  }

  override fun bulkInsert(users: List<User>) {
    println("save users ${users}")
  }
}
