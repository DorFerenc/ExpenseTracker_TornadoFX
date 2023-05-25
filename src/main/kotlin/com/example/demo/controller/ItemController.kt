package com.example.demo.controller

import com.example.demo.model.ExpensesEntry
import com.example.demo.model.ExpensesEntryModel
import com.example.demo.model.ExpensesEntryTbl
import com.example.demo.model.toExpensesEntry
import com.example.demo.util.execute
import javafx.collections.ObservableList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import tornadofx.*
import java.math.BigDecimal
import java.time.LocalDate

class ItemController: Controller() {

    //Get All items!!
    private val listOfItems: ObservableList<ExpensesEntryModel> = execute {
        ExpensesEntryTbl.selectAll().map {
            ExpensesEntryModel().apply {
                item = it.toExpensesEntry()
            }
        }.asObservable()
    }

    var items: ObservableList<ExpensesEntryModel> by singleAssign() // interface to use list of items

    var expenseModel = ExpensesEntryModel()

    init {
        items = listOfItems
//        add(LocalDate.now(),"Pants", 253.45)
//        add(LocalDate.now(),"Banana", 23.45)
//        add(LocalDate.now(),"Jewelery", 29843.45)
//        listOfItems.forEach {
//            println("Item::: ${it.itemName.value}")
//        }
    }

    fun add(newEntryDate: LocalDate, newItem: String, newPrice: Double): ExpensesEntry {
        val newEntry = execute {
            ExpensesEntryTbl.insert {
                it[entryDate] = newEntryDate
                it[itemName] = newItem
                it[itemPrice] = BigDecimal.valueOf(newPrice)
            }
        }
        val newEntryAsExpensesEntry = ExpensesEntry(newEntry[ExpensesEntryTbl.id], newEntryDate, newItem, newPrice)
        listOfItems.add(ExpensesEntryModel().apply { item = newEntryAsExpensesEntry })
        return newEntryAsExpensesEntry
    }

    fun update(updatedItem: ExpensesEntryModel): Int {
        return execute {
            ExpensesEntryTbl.update ({ ExpensesEntryTbl.id eq(updatedItem.id.value.toInt()) }) {
                it[entryDate] = updatedItem.entryDate.value
                it[itemName] = updatedItem.itemName.value
                it[itemPrice] = BigDecimal.valueOf(updatedItem.itemPrice.value.toDouble())
            }
        }
    }

    fun delete(model: ExpensesEntryModel) {
        execute {
            ExpensesEntryTbl.deleteWhere {
                ExpensesEntryTbl.id eq(model.id.value.toInt())
            }
        }
        listOfItems.remove(model)
    }
}