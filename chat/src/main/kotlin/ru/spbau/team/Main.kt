package ru.spbau.team

import javafx.application.Application

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Application.launch(View::class.java, *args)
    }
}