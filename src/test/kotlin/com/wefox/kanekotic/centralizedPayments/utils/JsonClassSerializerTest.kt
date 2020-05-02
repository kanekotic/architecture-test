package com.wefox.kanekotic.centralizedPayments.utils

import org.apache.kafka.common.serialization.Serializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JsonClassSerializerTest {

    private lateinit var subject: Serializer<Example>

    data class Example(val text: String)

    @BeforeEach
    fun setup() {
        subject = JsonClassSerializer()
    }

    @Test
    fun shouldBeAbleToConvertObject() {
        val result = subject.serialize("", Example("pepe"))
        Assertions.assertNotNull(result)
    }

}