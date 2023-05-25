package com.example.demo.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import tornadofx.*
import java.time.LocalDate

fun ResultRow.toExpensesEntry() = ExpensesEntry(
    this[ExpensesEntryTbl.id],
    this[ExpensesEntryTbl.entryDate], //.toJavaLocalDate(), // TODO check date
    this[ExpensesEntryTbl.itemName],
    this[ExpensesEntryTbl.itemPrice].toDouble()
)

/**
 * Represents the table structure for expenses entries.
 */
object ExpensesEntryTbl : Table() {
    val id = integer("id").autoIncrement()
    val entryDate = date("entry_date")
    val itemName = varchar("name", length = 50)
    val itemPrice = decimal("price", scale = 2, precision = 9)

    override val primaryKey = PrimaryKey(id, name = "PK_ExpensesEntryTbl_Id")
}

/**
 * Represents an expense entry.
 * @param id The ID of the expense entry.
 * @param entryDate The date of the expense entry.
 * @param itemName The name of the item.
 * @param itemPrice The price of the item.
 */
class ExpensesEntry(id: Int, entryDate: LocalDate, itemName: String, itemPrice: Double) {
    val idProperty = SimpleIntegerProperty(id)
    var id by idProperty

    val entryDateProperty = SimpleObjectProperty<LocalDate>(entryDate)
    var entryDate by entryDateProperty

    val itemNameProperty = SimpleStringProperty(itemName)
    var itemName by itemNameProperty

    val itemPriceProperty = SimpleDoubleProperty(itemPrice)
    var itemPrice by itemPriceProperty

    var totalExpenses = Bindings.add(itemPriceProperty, 0)

    /**
     * Returns a string representation of the expense entry.
     */
    override fun toString(): String {
        return "ExpensesEntry(id=$id, entryDate=$entryDate, itemName=$itemName, itemPrice=$itemPrice"
    }
}

/**
 * Represents the model for an expense entry.
 */
class ExpensesEntryModel: ItemViewModel<ExpensesEntry>() {
    val id = bind { item?.idProperty }
    val entryDate = bind { item?.entryDateProperty }
    val itemName = bind { item?.itemNameProperty }
    val itemPrice = bind { item?.itemPriceProperty }
    var totalExpenses = itemProperty.select(ExpensesEntry::totalExpenses)
}