package ru.spbau.team

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

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
            json.stringify(Message.serializer(), message).toByteArray(Charsets.UTF_8)
        )
    }

    fun close() {
        channel.close()
    }
}
