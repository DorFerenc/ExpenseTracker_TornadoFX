package com.example.demo.app

import com.example.demo.controller.ItemController
import com.example.demo.util.createTables
import com.example.demo.util.enableConsoleLogger
import com.example.demo.view.ExpensesEditor
import com.example.demo.view.ExpensesReport
import javafx.scene.control.TabPane
import org.jetbrains.exposed.sql.Database
import tornadofx.*


/**
 * The main application class representing the Budget Tracker workspace.
 * It extends the TornadoFX `Workspace` class and serves as the entry point for the application.
 *
 * @param title The title of the application window.
 * @param navigationMode The navigation mode for the workspace. In this case, it is set to `NavigationMode.Tabs`.
 */
class BudgetTrackerWorkspace : Workspace("Budget Tracker Workerspace", NavigationMode.Tabs) {

    init {
        // Enable console logging and set up the database
        enableConsoleLogger()
        Database.connect("jdbc:sqlite:./app-budget-tracker.db", "org.sqlite.JDBC")
        createTables()

        // Initialize the controllers
        ItemController()

        // Dock the views
        dock<ExpensesEditor>()
        dock<ExpensesReport>()

        // Configure the tab container
        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
    }
}
