package com.example.demo.model

import com.example.demo.util.toJavaLocalDate

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate
import tornadofx.*

fun ResultRow.toExpensesEntry() = ExpensesEntry(
    this[ExpensesEntryTbl.id],
    this[ExpensesEntryTbl.entryDate], //.toJavaLocalDate(), // TODO check date
    this[ExpensesEntryTbl.itemName],
    this[ExpensesEntryTbl.itemPrice].toDouble()
)

object ExpensesEntryTbl : Table() {
    val id = integer("id").autoIncrement()
    val entryDate = date("entry_date")
    val itemName = varchar("name", length = 50)
    val itemPrice = decimal("price", scale = 2, precision = 9)

    override val primaryKey = PrimaryKey(id, name = "PK_ExpensesEntryTbl_Id")
}

class ExpensesEntry(id: Int, entryDate: LocalDate, itemName: String, itemPrice: Double) {
    val idProperty = SimpleIntegerProperty(id)
    var id by idProperty

    val entryDateProperty = SimpleObjectProperty<LocalDate>(entryDate)
    var entryDate by entryDateProperty

    val itemNameProperty = SimpleStringProperty(itemName)
    var itemName by itemNameProperty

    val itemPriceProperty = SimpleDoubleProperty(itemPrice)
    var itemPrice by itemPriceProperty

    override fun toString(): String {
        return "ExpensesEntry(id=$id, entryDate=$entryDate, itemName=$itemName, itemPrice=$itemPrice"
    }
}

class ExpensesEntryModel: ItemViewModel<ExpensesEntry>() {
    val id = bind { item?.idProperty }
    val entryDate = bind { item?.entryDateProperty }
    val itemName = bind { item?.itemNameProperty }
    val itemPrice = bind { item?.itemPriceProperty }

}