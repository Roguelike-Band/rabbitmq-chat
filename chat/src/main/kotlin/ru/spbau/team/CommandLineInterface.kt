package ru.spbau.team

import com.beust.jcommander.JCommander

class CommandLineInterface {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val arguments = CliArguments()
            JCommander.newBuilder().addObject(arguments).build().parse(*args)
            val telekilogram = Telekilogram(arguments.address, arguments.login, arguments.password)
            val channel = telekilogram.subscribeOrCreateChannel("randdmChannel")
            while (true) {
                val chat = readLine()!!
                val message = readLine()!!
                channel.sendMessage(message)
            }
        }
    }
}