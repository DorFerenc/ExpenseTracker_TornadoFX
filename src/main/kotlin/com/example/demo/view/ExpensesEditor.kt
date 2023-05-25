package com.example.demo.view

//import com.example.demo.controller.ItemController
//import com.example.demo.model.ExpensesEntryModel
//import javafx.beans.binding.Bindings
//import javafx.beans.property.SimpleDoubleProperty
//import javafx.geometry.Pos
//import javafx.scene.input.KeyCode
//import tornadofx.*
//import java.awt.Label

import com.example.demo.controller.ItemController
import com.example.demo.model.ExpensesEntryModel
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import kotlin.Exception

class ExpensesEditor : View("Expenses") {

    private val model = ExpensesEntryModel()
    private val controller: ItemController by inject()
    private var mTableView: TableViewEditModel<ExpensesEntryModel> by singleAssign()
    private var totalExpensesLabel: Label by singleAssign()
    private val totalExpensesProperty = SimpleDoubleProperty(0.0)

    init {
       updateTotalExpenses()
    }

    override val root = borderpane {
        disableDelete()
        disableSave()
        disableCreate()
        disableClose()
        disableRefresh()

        center = vbox {
            form {
                // Fields to enter data
                fieldset {
                    field("Entry Date") {
                        maxWidth = 220.0
                        datepicker(model.entryDate) {
                            this.required()
                            validator {
                                when {
                                    it?.dayOfMonth.toString().isEmpty()
                                            || it?.dayOfWeek.toString().isEmpty()
                                            || it?.dayOfYear.toString().isEmpty() ->
                                        error("The date entry cannot be blank")

                                    else -> null
                                }
                            }
                        }
                    }
                }
                fieldset {
                    field("Item") {
                        maxWidth = 220.0
                        textfield(model.itemName) {
                            this.required()
                            validator {
                                when {
                                    it.isNullOrEmpty() -> error("Field cannot be empty")
                                    it.length < 3 -> error("Too short")
                                    else -> null
                                }
                            }
                        }
                    }
                }
                fieldset {
                    field("Price") {
                        maxWidth = 220.0
                        textfield(model.itemPrice) {
                            this.required()
                            validator {
                                when (it) { // passing it property into when so no need to type `it.`
                                    null -> error("Price cannot be blank")
                                    else -> null
                                }
                            }
                            setOnKeyPressed {
                                if (it.code == KeyCode.ENTER) {
                                    model.commit {
                                        addItem()
                                        model.rollback()
                                    }
                                }
                            }
                        }
                    }
                }

                // Buttons
                hbox(10.0) {
                    button("Add Item") {
                        enableWhen(model.valid)
                        action {
                            model.commit {
                                addItem()
                                model.rollback()
                            }
                        }
                    }
                    button("Delete") {
                        action {
                            when (val selectedItem = mTableView.tableView.selectedItem) {
                                null -> return@action
                                else -> {
                                    val diff = totalExpensesProperty.value - selectedItem.item.itemPrice
                                    totalExpensesProperty.value = diff
                                    controller.delete(selectedItem)
                                    updateTotalExpenses()
                                }
                            }
                        }
                    }
                    button("Reset") {
                        enableWhen(model.valid)
                        action {
                            model.commit {
                                model.rollback()
                            }
                        }
                    }
                }

                // table view to see the results
                fieldset {
                    vboxConstraints { marginTop = 20.0 }
                    tableview<ExpensesEntryModel> {
                        items = controller.items
                        mTableView = editModel // connecting  mTable to this table and enabling to edit
                        column("ID", ExpensesEntryModel::id)
                        column("Added", ExpensesEntryModel::entryDate).makeEditable()
                        column("Name", ExpensesEntryModel::itemName).makeEditable()
                        column("Price", ExpensesEntryModel::itemPrice).makeEditable()

                        onEditCommit {
                            controller.update(it)
                            updateTotalExpenses()
                            controller.updatePiecePie(it)
                        }
                    }
                }
            }
        }

        right = vbox(10) {
            alignment = Pos.CENTER
            paddingBottom = 10.0

            piechart("Total Expenses") { data = controller.pieItemsData }

            totalExpensesLabel = label {
//                vboxConstraints { marginTop = 20.0 }
                if (totalExpensesProperty.doubleValue() != 0.0) {
                    style {
                        fontSize = 19.px
                        padding = box(10.px)
                        textFill = Color.GREEN
                        fontWeight = FontWeight.EXTRA_BOLD
                        borderRadius = multi(box(8.px))
                        backgroundColor += c("white", 0.8)

                    }
                    bind(Bindings.concat("Total Expenses: ", "$ ", Bindings.format("%.2f", totalExpensesProperty)))
                } else {
                    // do nothing
                }
            }
        }
    }

    private fun updateTotalExpenses() {
        var total = 0.0
        try {
            // fetch all expenses
            controller.items.forEach { total += it.itemPrice.value.toDouble() }
            totalExpensesProperty.set(total)
            model.totalExpenses.value = total
        }catch ( e: Exception) { totalExpensesProperty.set(0.0) }
    }

    private fun addItem() {
        controller.add(model.entryDate.value, model.itemName.value, model.itemPrice.value.toDouble())
        updateTotalExpenses()
    }

    // to override the buttons on the top


//    override fun onRefresh() {
//        super.onRefresh()
//    }
//
//    override fun onDelete() {
//        super.onDelete()
//    }
//
//    override fun onSave() {
//        super.onSave()
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//    }
}
