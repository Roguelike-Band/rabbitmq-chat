package ru.spbau.team

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class Telekilogram(serverIP: String, serverLogin: String, serverPassword: String) {
    private val factory = ConnectionFactory()
    private val connection: Connection
    private val channelNameToChannel = mutableMapOf<String, TelekilogramChannel>()

    init {
        factory.host = serverIP
        factory.password = serverLogin
        factory.username = serverPassword
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
