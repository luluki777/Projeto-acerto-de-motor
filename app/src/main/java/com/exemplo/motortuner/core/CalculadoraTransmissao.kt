package com.exemplo.motortuner.core

import kotlin.math.PI

object CalculadoraTransmissao {

    /**
     * Circunferência do pneu em metros.
     * diametroPneuMm = aro (polegadas × 25,4) + 2 × perfil (mm)
     */
    fun circunferenciaPneu(aroPolegadas: Double, larguraMm: Double, perfil: Double): Double {
        val aroMm = aroPolegadas * 25.4
        val alturaLateralMm = larguraMm * perfil / 100.0
        val diametroMm = aroMm + 2.0 * alturaLateralMm
        return PI * diametroMm / 1000.0
    }

    /** Velocidade em km/h. */
    fun velocidadeKmh(
        rpm: Int,
        circunferenciaM: Double,
        relacaoMarcha: Double,
        relacaoDiferencial: Double
    ): Double =
        (rpm * circunferenciaM * 60.0) / (1000.0 * relacaoMarcha * relacaoDiferencial)

    /** RPM para uma dada velocidade. */
    fun rpmParaVelocidade(
        velocidadeKmh: Double,
        circunferenciaM: Double,
        relacaoMarcha: Double,
        relacaoDiferencial: Double
    ): Double =
        (velocidadeKmh * 1000.0 * relacaoMarcha * relacaoDiferencial) / (circunferenciaM * 60.0)

    data class LinhaMarcha(
        val marcha: Int,
        val relacao: Double,
        val rpm: Int,
        val velocidadeKmh: Double
    )

    fun tabelaPorMarcha(
        rpm: Int,
        circunferenciaM: Double,
        relacaoDiferencial: Double,
        relacoes: List<Double>
    ): List<LinhaMarcha> =
        relacoes.mapIndexed { i, rel ->
            LinhaMarcha(
                marcha = i + 1,
                relacao = rel,
                rpm = rpm,
                velocidadeKmh = velocidadeKmh(rpm, circunferenciaM, rel, relacaoDiferencial)
            )
        }
}
