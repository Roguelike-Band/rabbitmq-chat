package ru.spbau.team

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
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

class CliArguments {
    @Parameter(
        names = ["-address"]
    )
    var address: String = "127.0.0.1"
        private set

    @Parameter(
        names = ["-login"]
    )
    var login: String = "guest"
        private set

    @Parameter(
        names = ["-password"]
    )
    var password: String = "guest"
        private set

    @Parameter(
        required = true
    )
    private var otherArguments: MutableList<String> = mutableListOf()

    fun getNickname() = otherArguments[0]
}

fun main(args: Array<String>) {
    val arguments = CliArguments()
    JCommander.newBuilder().addObject(arguments).build().parse(*args)
    val telekilogram = Telekilogram(arguments.address, arguments.login, arguments.password)
    val channel = telekilogram.subscribeOrCreateChannel("randdmChannel")
    while (true) {
        val message = readLine()!!
        channel.sendMessage(message)
    }
}
