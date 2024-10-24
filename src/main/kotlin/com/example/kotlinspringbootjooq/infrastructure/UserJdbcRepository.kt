package com.example.kotlinspringbootjooq.infrastructure

import com.example.kotlinspringbootjooq.domain.Email
import com.example.kotlinspringbootjooq.domain.User
import com.example.kotlinspringbootjooq.domain.UserId
import com.example.kotlinspringbootjooq.domain.UserName
import com.example.kotlinspringbootjooq.domain.UserRepository
import com.example.kotlinspringbootjooq.jooq.tables.Users.USERS
import com.example.kotlinspringbootjooq.jooq.tables.records.UsersRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Component

@Component
class UserJdbcRepository(
  val jooq: DSLContext
) : UserRepository {
  override fun findById(id: UserId): User? {
    return jooq.selectFrom(USERS)
      .where(USERS.ID.eq(id.value))
      .fetchOne()
      ?.toDomain()
  }

  override fun insert(user: User) {
    jooq.batchInsert(listOf(user.toRecord())).execute()
  }

  override fun bulkInsert(users: List<User>) {
    jooq.batchInsert(users.map { it.toRecord() }).execute()
  }
}

fun UsersRecord.toDomain(): User {
  return User(
    id = UserId.of(this.id),
    userName = UserName.of(this.userName),
    email = Email.of(this.email),
  )
}

fun User.toRecord(): UsersRecord {
  return UsersRecord(
    this.id.value,
    this.userName.value,
    this.email.value,
  )
}
