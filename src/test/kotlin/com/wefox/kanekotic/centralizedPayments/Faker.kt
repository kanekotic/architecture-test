package com.wefox.kanekotic.centralizedPayments

import com.wefox.kanekotic.centralizedPayments.models.Payment

object Faker {

    fun payment() : Payment {
        return Payment("123245", 678, "offline", "91011", 100.1, 10)
    }
}