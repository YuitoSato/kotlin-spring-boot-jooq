package com.example.kotlinspringbootjooq.domain

interface UserRepository {

    fun findById(id: UserId): User?

  fun insert(user: User)

  fun bulkInsert(users: List<User>)
}
