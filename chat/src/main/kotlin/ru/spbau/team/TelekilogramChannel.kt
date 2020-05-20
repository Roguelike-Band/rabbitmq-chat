package ru.spbau.team

import com.rabbitmq.client.*
import javafx.application.Platform
import javafx.collections.FXCollections
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class TelekilogramChannel(
    val channelName: String,
    private val channel: Channel,
    private val queueName: String,
    private val connection: Connection
) {
    val messages = FXCollections.observableArrayList<Message>()

    @Synchronized
    private fun addMessage(message: Message) {
        Platform.runLater {
            messages.add(message)
        }
    }

    init {
        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String,
                envelope: Envelope,
                properties: AMQP.BasicProperties,
                body: ByteArray
            ) {
                val json = Json(JsonConfiguration.Stable)
                val messageJson = String(body, Charsets.UTF_8)
                val message = json.parse(Message.serializer(), messageJson)
                addMessage(message)
                println(message)
            }
        }
        channel.basicConsume(queueName, true, consumer)
    }

    fun sendMessage(message: Message) {
        val json = Json(JsonConfiguration.Stable)
        channel.basicPublish(
            channelName,
            "",
            null,
            json.stringify(Message.serializer(), message).toByteArray(Charsets.UTF_8)
        )
    }

    fun close() {
        channel.close()
        connection.close()
    }
}
