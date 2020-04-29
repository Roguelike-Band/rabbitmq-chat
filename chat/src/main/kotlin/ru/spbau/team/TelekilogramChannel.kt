package ru.spbau.team

import com.rabbitmq.client.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.*

class TelekilogramChannel(
    val channelName: String,
    private val channel: Channel,
    private val queueName: String
) {
    private val messages = mutableListOf<String>()

    @Synchronized
    private fun addMessage(message: String) {
        messages.add(message)
    }

    @Synchronized
    fun getMessages(): List<String> {
        return messages
    }

    init {
        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String,
                envelope: Envelope,
                properties: AMQP.BasicProperties,
                body: ByteArray
            ) {
                val message = String(body, Charsets.UTF_8)
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
            json.stringify(Message.serializer(), message).toByteArray()
        )
    }

    fun close() {
        channel.close()
    }
}
