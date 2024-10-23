package com.example.kotlinspringbootjooq.domain

interface UserRepository {

  fun insert(user: User)

  fun bulkInsert(users: List<User>)
}
