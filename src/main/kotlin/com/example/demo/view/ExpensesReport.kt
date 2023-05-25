package com.example.demo.view

import com.example.demo.controller.ItemController
import com.example.demo.model.ExpensesEntryModel
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.PieChart
import javafx.scene.control.ComboBox
import tornadofx.*
import java.time.LocalDate

class ExpensesReport : View("Report") {
    val controller : ItemController by inject()
    var comboBox: ComboBox<String> by singleAssign()

    private var listOfItems: ObservableList<ExpensesEntryModel> by singleAssign()
    private var pieData = FXCollections.observableArrayList<PieChart.Data>()

    val today = LocalDate.of(LocalDate.now().year, LocalDate.now().month, LocalDate.now().dayOfMonth)
    init {

        listOfItems = controller.filterByEntryDates(today)
        listOfItems.forEach {
            pieData.add(PieChart.Data(it.itemName.value, it.itemPrice.value.toDouble()))
        }
    }
    override val root = borderpane {
        disableDelete()
        disableSave()
        disableCreate()
        disableClose()
        disableRefresh()


        left{
            form {
                fieldset {
                    field("Filter By:") {
                        maxWidth = 200.0
                        comboBox = combobox(values = listOf("Today",
                            "Yesterday", "Week", "Month", "Year")) {
                            prefWidth = 135.0
                            value = "Today"
                            this.valueProperty().onChange {
                                getFilteredItems(it)
                                updatePie()
                            }

                        }
                    }
                }
            }
        }

        center {
            tableview<ExpensesEntryModel> {
                items = listOfItems
                column("Entry Date:", ExpensesEntryModel::entryDate)
                column("Item", ExpensesEntryModel::itemName)
                column("Price", ExpensesEntryModel::itemPrice)
            }
        }

        right {
            piechart("Expenses Report", data = pieData)
        }

    }

    private fun getFilteredItems(choice: String?) {
        when(choice) {
            "Today" -> listOfItems.setAll(controller.filterByEntryDates(today))
            "Yesterday" -> listOfItems.setAll(controller.filterByEntryDates(today.minusDays(1)))
            "Week" -> listOfItems.setAll(controller.filterByEntryDates(today.minusWeeks(1)))
            "Month" -> listOfItems.setAll(controller.filterByEntryDates(today.minusMonths(1)))
            "Year" -> listOfItems.setAll(controller.filterByEntryDates(today.minusYears(1)))
        }


        updatePie()
        listOfItems.forEach {
            pieData.setAll()
        }


    }

    private fun updatePie() {
        listOfItems.forEach {
            pieData.add(PieChart.Data(it.itemName.value, it.itemPrice.value.toDouble()))
        }
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
