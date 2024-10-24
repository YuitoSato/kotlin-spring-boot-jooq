package com.example.kotlinspringbootjooq.utils

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.interceptor.TransactionAspectSupport

@Retention(AnnotationRetention.RUNTIME)
@Transactional(propagation = Propagation.REQUIRED)
annotation class ResultTransactional

// Aspect to handle Result rollback logic
@Component
@Aspect
class ResultTransactionalAspect {

    @Around("@annotation(ResultTransactional)")
    fun handleResultTransaction(joinPoint: ProceedingJoinPoint): Any? {
        val proceeded = joinPoint.proceed()

        val isErr = proceeded.javaClass.name == "com.github.michaelbull.result.Failure"

        if (isErr) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly()
        }

        return proceeded
    }
}
