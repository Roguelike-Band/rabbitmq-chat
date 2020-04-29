package ru.spbau.team

import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class Telekilogram(serverIP: String, serverLogin: String, serverPassword: String) {
    private val factory = ConnectionFactory()
    private val channelNameToChannel = mutableMapOf<String, TelekilogramChannel>()

    init {
        factory.host = serverIP
        factory.password = serverLogin
        factory.username = serverPassword
    }

    fun subscribeOrCreateChannel(channelName: String): TelekilogramChannel {
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.exchangeDeclare(channelName, "fanout")

        val newQueue = channel.queueDeclare().queue
        channel.queueBind(newQueue, channelName, "")
        val createdChannel = TelekilogramChannel(channelName, channel, newQueue, connection)
        channelNameToChannel[channelName] = createdChannel
        return createdChannel
    }

    fun close() {
        for (channel in channelNameToChannel) {
            channel.value.close()
        }
    }
}
