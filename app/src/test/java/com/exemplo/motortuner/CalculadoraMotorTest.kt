package com.exemplo.motortuner

import com.exemplo.motortuner.core.CalculadoraMotor
import com.exemplo.motortuner.core.Numeros
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculadoraMotorTest {

    @Test
    fun cilindrada_81x969_4cilindros() {
        val unit = CalculadoraMotor.cilindradaUnitaria(81.0, 96.9)
        val total = CalculadoraMotor.cilindradaTotal(81.0, 96.9, 4)
        assertEquals(499.33, unit, 0.05)
        assertEquals(1997.30, total, 0.1)
    }

    @Test
    fun cilindrada_1cilindro() {
        val total = CalculadoraMotor.cilindradaTotal(50.0, 50.0, 1)
        assertEquals(98.17, total, 0.1)
    }

    @Test
    fun cilindrada_2cilindros() {
        val total = CalculadoraMotor.cilindradaTotal(70.0, 80.0, 2)
        assertEquals(615.75, total, 0.5)
    }

    @Test
    fun velocidadePistao_969mm_6500rpm() {
        val v = CalculadoraMotor.velocidadeMediaPistao(96.9, 6500)
        assertEquals(20.995, v, 0.05)
    }

    @Test
    fun parser_virgula_decimal() {
        assertEquals(96.9, Numeros.parse("96,9")!!, 0.001)
    }

    @Test
    fun parser_ponto_decimal() {
        assertEquals(96.9, Numeros.parse("96.9")!!, 0.001)
    }

    @Test
    fun parser_ponto_milhar() {
        assertEquals(6500.0, Numeros.parse("6.500")!!, 0.001)
    }

    @Test
    fun parser_inteiro_simples() {
        assertEquals(6500.0, Numeros.parse("6500")!!, 0.001)
    }

    @Test
    fun parser_misto_1234_56() {
        assertEquals(1234.56, Numeros.parse("1.234,56")!!, 0.001)
    }

    @Test
    fun parser_invalido_retorna_null() {
        assertNull(Numeros.parse("abc"))
        assertNull(Numeros.parse(""))
        assertNull(Numeros.parse("   "))
    }
}
