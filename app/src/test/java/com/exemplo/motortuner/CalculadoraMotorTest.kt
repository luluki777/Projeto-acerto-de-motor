package com.exemplo.motortuner

import com.exemplo.motortuner.core.CalculadoraMotor
import com.exemplo.motortuner.core.Numeros
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculadoraMotorTest {

    @Test
    fun cilindrada_81x969_4cilindros() {
        val unit = CalculadoraMotor.c
