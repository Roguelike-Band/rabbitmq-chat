package ru.spbau.team

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Message(
        val senderName: String,
        val timestamp: Date,
        val message: String
)