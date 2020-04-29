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
            while (true) {
                val chat = readLine()!!
                val message = readLine()!!
                val channel = telekilogram.subscribeOrCreateChannel(chat)
                channel.sendMessage(Message(arguments.getNickname(), Date(), message))
            }
        }
    }
}