package ru.spbau.team

import com.beust.jcommander.JCommander
import java.util.*

class CommandLineInterface {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val arguments = CliArguments()
            JCommander.newBuilder().addObject(arguments).build().parse(*args)
            val telekilogram = Telekilogram(arguments.address, arguments.login, arguments.password)
            val chats = mutableMapOf<String, TelekilogramChannel>()
            while (true) {
                val chat = readLine()!!
                val chatChannel = chats[chat] ?: telekilogram.subscribeOrCreateChannel(chat)
                chats[chat] = chatChannel
                val message = readLine()!!
                chatChannel.sendMessage(Message(arguments.getNickname(), Date(), message))
            }
        }
    }
}