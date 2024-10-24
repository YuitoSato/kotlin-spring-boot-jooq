package com.example.kotlinspringbootjooq.utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.partition

fun <V, E> List<Result<V, E>>.combineOrAccumulate(): Result<List<V>, List<E>> {
    val (values, errors) = this.partition()
    return if (errors.isEmpty()) {
        Ok(values)
    } else {
        Err(errors)
    }
}
