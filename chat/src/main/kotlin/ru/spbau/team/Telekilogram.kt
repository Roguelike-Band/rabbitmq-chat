package ru.spbau.team

import com.rabbitmq.client.ConnectionFactory
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory


class Telekilogram(serverIP: String) {
    private val factory = ConnectionFactory()
    private val channelNameToChannel = mutableMapOf<String, TelekilogramChannel>()

    init {
        factory.host = serverIP
        factory.port = 5671

        val keyPassphrase = "password".toCharArray()
        val ks = KeyStore.getInstance("PKCS12")
        ks.load(FileInputStream("../result/client_key.p12"), keyPassphrase)

        val kmf = KeyManagerFactory.getInstance("SunX509")
        kmf.init(ks, keyPassphrase)

        val trustPassphrase = "rabbitstore".toCharArray()
        val tks = KeyStore.getInstance("JKS")
        tks.load(FileInputStream("../result/rabbitstore"), trustPassphrase)

        val tmf = TrustManagerFactory.getInstance("SunX509")
        tmf.init(tks)

        val c = SSLContext.getInstance("TLSv1.2")
        c.init(kmf.keyManagers, tmf.trustManagers, null)

        factory.useSslProtocol(c)
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
