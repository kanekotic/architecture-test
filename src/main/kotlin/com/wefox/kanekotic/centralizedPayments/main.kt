package com.wefox.kanekotic.centralizedPayments

import com.wefox.kanekotic.centralizedPayments.KafkaConfiguration.streamsConfig
import com.wefox.kanekotic.centralizedPayments.models.Toggles
import com.wefox.kanekotic.centralizedPayments.persistors.PaymentPersistor
import com.wefox.kanekotic.centralizedPayments.processors.SavePaymentProcessor
import com.wefox.kanekotic.centralizedPayments.serdes.PaymentSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import java.util.concurrent.CountDownLatch


fun main() {
    val props = streamsConfig
    val builder = StreamsBuilder()
    val toggles = Toggles(offline = false, online = false)
    val paymentSerde = PaymentSerde.get()

    if (toggles.offline) {
        val paymentPersistor = PaymentPersistor()
        val stream = builder.stream(KafkaConfiguration.OFFLINE_INPUT_TOPIC, Consumed.with(Serdes.String(), paymentSerde.serde))
            .peek { key, value ->
                println("key = $key, value = $value")
            }
        SavePaymentProcessor(stream, paymentPersistor)
    }

    if (toggles.online) {
        builder.stream(KafkaConfiguration.ONLINE_INPUT_TOPIC, Consumed.with(Serdes.String(), paymentSerde.serde)).peek { key, value ->
            println("key = $key, value = $value")
        }
    }
    val streams = KafkaStreams(builder.build(), props)
    val latch = CountDownLatch(1)

    Runtime.getRuntime().addShutdownHook(object : Thread("streams-centralized-payments-shutdown-hook") {
        override fun run() {
            streams.close()
            latch.countDown()
        }
    })
    try {
        streams.start()
        latch.await()
    } catch (e: Throwable) {
        System.exit(1)
    }
    System.exit(0)
}