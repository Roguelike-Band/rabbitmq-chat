package ru.spbau.team

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import java.text.SimpleDateFormat
import java.util.*

@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
        override val descriptor: SerialDescriptor = SerialDescriptor("DateSerializer")

        override fun serialize(encoder: Encoder, value: Date) {
                encoder.encodeString(value.time.toString())
        }

        override fun deserialize(decoder: Decoder): Date {
                return Date(decoder.decodeString().toLong())
        }
}

@Serializable
data class Message(
        val senderName: String,
        @Serializable(with = DateSerializer::class)  val timestamp: Date,
        val message: String
) {
        override fun toString(): String {
                val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
                return "[${sdf.format(timestamp)}] ${senderName}: $message"
        }
}
