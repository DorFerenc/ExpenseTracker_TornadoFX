package com.example.demo.view

import com.example.demo.controller.ItemController
import com.example.demo.model.ExpensesEntryModel
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import tornadofx.*

class ExpensesEditor : View("Expenses") {

    private val model = ExpensesEntryModel()
    private val controller: ItemController by inject()
    private var mTableView: TableViewEditModel<ExpensesEntryModel> by singleAssign()

    override val root = borderpane {

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
                                    it!!.length < 3 -> error("Too short")
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
                            val selectedItem = mTableView.tableView.selectedItem
                            controller.delete(selectedItem!!)
                        }
                    }
                    button("Reset") {
                        action { }
                    }
                }

                // table view to see the results
                fieldset() {
                    vboxConstraints { marginTop = 20.0}
                    tableview<ExpensesEntryModel> {
                        items = controller.items
                        mTableView = editModel // connecting  mTable to this table and enabling to edit
                        column("ID", ExpensesEntryModel::id)
                        column("Added", ExpensesEntryModel::entryDate).makeEditable()
                        column("Name", ExpensesEntryModel::itemName).makeEditable()
                        column("Price", ExpensesEntryModel::itemPrice).makeEditable()

                        onEditCommit {
                            controller.update(it)
                            controller.updatePiecePie(it)
                        }
                    }
                }
            }
        }

        right = vbox {
            alignment = Pos.CENTER
            piechart {
//                data("Bananas",120.0)
                data = controller.pieItemsData
            }
        }
    }

    private fun addItem() {
        controller.add(model.entryDate.value, model.itemName.value, model.itemPrice.value.toDouble())
    }
}
