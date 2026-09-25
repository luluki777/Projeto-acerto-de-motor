package com.exemplo.motortuner.core

import kotlin.math.PI

object CalculadoraDesempenho {

    /** BMEP em bar. */
    fun bmep(torqueNm: Double, cilindradaTotalL: Double): Double =
        (torqueNm * 4.0 * PI) / (cilindradaTotalL / 1000.0) / 100000.0

    /** Potência específica em hp/L. */
    fun potenciaEspecifica(potenciaHp: Double, cilindradaTotalL: Double): Double =
        potenciaHp / cilindradaTotalL

    /** Torque em Nm a partir de potência em hp e RPM. */
    fun torqueDePotencia(potenciaHp: Double, rpm: Int): Double =
        (potenciaHp * 5252.0) / rpm * 1.3558

    /** Velocidade média do pistão em m/s (já existia no módulo Motor). */
    fun velocidadeMediaPistao(strokeMm: Double, rpm: Int): Double =
        2.0 * strokeMm * rpm / 60000.0

    /** Ponto de curva torque/potência estimados linearmente (estimativa simples). */
    data class PontoDesempenho(val rpm: Int, val torqueNm: Double, val potenciaHp: Double)

    /**
     * Gera uma curva estimada de torque e potência em 8 pontos.
     * O torque parte de 60% do pico em marcha lenta, sobe até 100% em rpmPicoTorque
     * e cai suavemente depois. A potência é derivada: hp = torque_Nm × rpm / 7127.
     */
    fun curvaEstimada(
        torquePicoNm: Double,
        rpmPicoTorque: Int,
        rpmMax: Int,
        pontos: Int = 8
    ): List<PontoDesempenho> {
        val rpmMin = 800
        val passo = (rpmMax - rpmMin) / (pontos - 1)
        return (0 until pontos).map { i ->
            val rpm = rpmMin + passo * i
            val frac = rpm.toDouble() / rpmPicoTorque
            val torque = when {
                rpm <= rpmPicoTorque -> {
                    // sobe de 0.6 a 1.0 do pico
                    val fator = 0.6 + 0.4 * (rpm.toDouble() / rpmPicoTorque)
                    torquePicoNm * fator
                }
                else -> {
                    // cai ~1% a cada 500 rpm acima do pico
                    val queda = ((rpm - rpmPicoTorque) / 500.0) * 0.01
                    torquePicoNm * (1.0 - queda).coerceAtLeast(0.55)
                }
            }
            val hp = torque * rpm / 7127.0
            PontoDesempenho(rpm, torque, hp)
        }
    }
}
