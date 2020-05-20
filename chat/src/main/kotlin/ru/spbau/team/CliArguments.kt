package ru.spbau.team

import com.beust.jcommander.Parameter

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
