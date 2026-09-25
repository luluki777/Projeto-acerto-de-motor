package com.exemplo.motortuner.core

object CalculadoraInjecao {

    /**
     * BSFC típico em lb/(hp·h):
     *   gasolina aspirado:  0,45 a 0,50
     *   gasolina turbo:     0,55 a 0,65
     *   etanol aspirado:    0,55 a 0,65
     *   etanol turbo:       0,65 a 0,75
     */
    fun bsfcSugerido(combustivel: String, aspirado: Boolean): Double {
        return when {
            combustivel.contains("Etanol", true) && aspirado -> 0.60
            combustivel.contains("Etanol", true) && !aspirado -> 0.70
            aspirado -> 0.48
            else -> 0.60
        }
    }

    /**
     * Vazão total necessária em lb/h.
     * Q_total = (Potência_hp × BSFC) / (duty cycle)
     */
    fun vazaoTotalLbH(potenciaHp: Double, bsfc: Double, dutyCycle: Double): Double =
        (potenciaHp * bsfc) / dutyCycle

    /** Vazão por bico em lb/h. */
    fun vazaoPorBicoLbH(potenciaHp: Double, bsfc: Double, dutyCycle: Double, nBicos: Int): Double =
        vazaoTotalLbH(potenciaHp, bsfc, dutyCycle) / nBicos

    /**
     * Converte lb/h para cc/min.
     * 1 lb/h ≈ 10,5 cc/min (gasolina, densidade 0,745 kg/L).
     */
    fun lbHParaCcMin(lbH: Double): Double = lbH * 10.5

    /** Converte cc/min para lb/h. */
    fun ccMinParaLbH(ccMin: Double): Double = ccMin / 10.5

    data class ResultadoInjecao(
        val vazaoTotalLbH: Double,
        val vazaoTotalCcMin: Double,
        val vazaoPorBicoLbH: Double,
        val vazaoPorBicoCcMin: Double,
        val bsfcUsado: Double,
        val dutyCycleUsado: Double
    )

    fun calcular(
        potenciaHp: Double,
        nCilindros: Int,
        bsfc: Double,
        dutyCycle: Double
    ): ResultadoInjecao {
        val nBicos = nCilindros
        val totalLbH = vazaoTotalLbH(potenciaHp, bsfc, dutyCycle)
        val porBicoLbH = totalLbH / nBicos
        return ResultadoInjecao(
            vazaoTotalLbH = totalLbH,
            vazaoTotalCcMin = lbHParaCcMin(totalLbH),
            vazaoPorBicoLbH = porBicoLbH,
            vazaoPorBicoCcMin = lbHParaCcMin(porBicoLbH),
            bsfcUsado = bsfc,
            dutyCycleUsado = dutyCycle
        )
    }
}
