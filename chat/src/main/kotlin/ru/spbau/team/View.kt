package ru.spbau.team

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Stage

class View : Application() {

    val messageList = FXCollections.observableArrayList(listOf("kek", "lol", "validol"))

    override fun start(p0: Stage) {
        p0.title = "Telekilogram"

        val pane = TabPane()

        val pane0 = GridPane()
        val channelName = TextArea()
        channelName.promptText = "Channel name"
        channelName.prefHeight = COMPOSE_HEIGHT
        channelName.prefWidth = WINDOW_WIDTH - SEND_WIDTH
        val subscribeButton =  Button("Subscribe")
        subscribeButton.prefHeight = COMPOSE_HEIGHT
        subscribeButton.prefWidth = SEND_WIDTH
        subscribeButton.onMouseClicked = EventHandler {
            if (channelName.text.isNotEmpty()) {
                // подписаться на канал
                val channel = channelName.text
                channelName.text = ""
                val panex = GridPane()

                val messageField = TextArea()
                messageField.promptText = "Message"
                messageField.prefHeight = COMPOSE_HEIGHT
                messageField.prefWidth = WINDOW_WIDTH - SEND_WIDTH
                val sendButton = Button("Send")
                sendButton.prefHeight = COMPOSE_HEIGHT
                sendButton.prefWidth = SEND_WIDTH
                sendButton.onMouseClicked = EventHandler {
                    if (messageField.text.isNotEmpty()) {
                        // отправить сообщеньку
                        messageField.text = ""
                    }
                }

                val messageListView = ListView(messageList)
                messageListView.prefHeight = WINDOW_HEIGHT - COMPOSE_HEIGHT
                panex.add(messageField, 0, 0)
                panex.add(sendButton, 1, 0)
                panex.add(messageListView, 0, 1, 2, 1)
                val tabx = Tab(channel)
                tabx.content = panex
                pane.tabs.add(tabx)
            }
        }
        pane0.add(channelName, 0, 0)
        pane0.add(subscribeButton, 1, 0)

        val tab0 = Tab("New channel")
        tab0.content = pane0
        pane.tabs.add(tab0)

        val scene = Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT)
        p0.scene = scene
        p0.show()
    }

    companion object {
        const val COMPOSE_HEIGHT = 80.0
        const val SEND_WIDTH = 100.0
        const val WINDOW_HEIGHT = 600.0
        const val WINDOW_WIDTH = 800.0
    }

}