package com.example.demo.util

import com.example.demo.model.ExpensesEntryTbl
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

private var LOG_TO_CONSOLE: Boolean = false

fun newTransaction(): Transaction =
    TransactionManager.currentOrNew(Connection.TRANSACTION_SERIALIZABLE).apply {
        if (LOG_TO_CONSOLE) addLogger(StdOutSqlLogger)
    }

fun enableConsoleLogger() {
    LOG_TO_CONSOLE = true
}

fun createTables() {
    with(newTransaction()) {
        SchemaUtils.create(ExpensesEntryTbl)
    }
}

fun <T> execute(command: () -> T) : T {
    with(newTransaction()) {
        return  command().apply {
            commit()
            close()
        }
    }
}