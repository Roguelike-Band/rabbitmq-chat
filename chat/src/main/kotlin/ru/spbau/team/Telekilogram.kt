package ru.spbau.team

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import java.util.*

class Telekilogram(serverIP: String) {
    private val factory = ConnectionFactory()
    private val connection: Connection
    private val channelNameToChannel = mutableMapOf<String, TelekilogramChannel>()

    init {
        factory.host = serverIP
        factory.password = "test"
        factory.username = "test"
        connection = factory.newConnection()
    }

    fun subscribeOrCreateChannel(channelName: String): TelekilogramChannel {
        val channel= connection.createChannel()
        channel.exchangeDeclare(channelName, "fanout")

        val newQueue = channel.queueDeclare().queue
        channel.queueBind(newQueue, channelName, "")

        val createdChannel = TelekilogramChannel(channelName, channel, newQueue)
        channelNameToChannel[channelName] = createdChannel
        return createdChannel
    }

    fun close() {
        for (channel in channelNameToChannel) {
            channel.value.close()
        }
        connection.close()
    }
}

fun main() {
    val telekilogram = Telekilogram("localhost")
    val channel = telekilogram.subscribeOrCreateChannel("randdmChannel")
    while (true) {
        val message = readLine()!!
        channel.sendMessage(Message("Igor", Date(), message))
    }
}
