package com.example.demo.app

import com.example.demo.controller.ItemController
import com.example.demo.util.createTables
import com.example.demo.util.enableConsoleLogger
import com.example.demo.view.ExpensesEditor
import com.example.demo.view.ExpensesReport
import javafx.scene.control.TabPane
import org.jetbrains.exposed.sql.Database
import tornadofx.*

class BudgetTrackerWorkspace : Workspace("Budget Tracker Workerspace", NavigationMode.Tabs) {

    init {
        //we initialize db etc...
        enableConsoleLogger()
        Database.connect("jdbc:sqlite:./app-budget-tracker.db", "org.sqlite.JDBC")
        createTables()


        //controller(es)
        ItemController()

        //dock our views
        dock<ExpensesEditor>()
        dock<ExpensesReport>()

        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }
}
