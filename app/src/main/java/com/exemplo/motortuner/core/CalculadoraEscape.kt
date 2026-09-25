package com.exemplo.motortuner.core

import kotlin.math.PI
import kotlin.math.sqrt

object CalculadoraEscape {

    fun areaSecao(diametroMm: Double): Double = PI / 4.0 * diametroMm * diametroMm

    /**
     * Velocidade dos gases em m/s, mesmo modelo da admissão.
     * Na prática os gases estão mais quentes (~600°C) e mais rápidos,
     * por isso o valor calculado é uma referência fria. Marcamos como estimativa.
     */
    fun velocidadeGases(
        cilindradaTotalCc: Double,
        rpm: Int,
        diametroEscapeMm: Double
    ): Double {
        val cilindradaM3 = cilindradaTotalCc / 1_000_000.0
        val areaM2 = areaSecao(diametroEscapeMm) / 1_000_000.0
        return (cilindradaM3 * rpm) / (2.0 * 60.0 * areaM2)
    }

    /**
     * Diâmetro sugerido por regra prática:
     * D (mm) ≈ √(Potência_hp) × k
     * k = 1,25 (rua) a 1,45 (esportivo)
     */
    fun diametroSugeridoPorPotencia(potenciaHp: Double, esportivo: Boolean): Double {
        val k = if (esportivo) 1.45 else 1.25
        return sqrt(potenciaHp) * k
    }

    /** Faixa em torno do diâmetro sugerido. */
    fun faixaDiametro(sugerido: Double): Pair<Double, Double> =
        Pair(sugerido * 0.92, sugerido * 1.10)
}
