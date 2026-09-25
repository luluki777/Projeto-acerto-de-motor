package com.exemplo.motortuner.core

import kotlin.math.PI
import kotlin.math.sqrt

object CalculadoraAdmissao {

    /** Área de seção circular em mm². */
    fun areaSecao(diametroMm: Double): Double = PI / 4.0 * diametroMm * diametroMm

    /**
     * Velocidade de fluxo em m/s, assumindo fluxo contínuo médio.
     * v = (cilindrada_unitária_m³ × RPM) / (60 × 2 × área_m²)
     * (divide por 2 porque é 4 tempos: uma admissão a cada 2 voltas)
     */
    fun velocidadeFluxo(
        cilindradaUnitariaCc: Double,
        rpm: Int,
        diametroDutoMm: Double
    ): Double {
        val cilindradaM3 = cilindradaUnitariaCc / 1_000_000.0
        val areaM2 = areaSecao(diametroDutoMm) / 1_000_000.0
        return (cilindradaM3 * rpm) / (2.0 * 60.0 * areaM2)
    }

    /**
     * Diâmetro de duto sugerido para atingir uma velocidade-alvo (m/s).
     * D = √(4 × Q / (π × v))
     */
    fun diametroSugerido(
        cilindradaUnitariaCc: Double,
        rpm: Int,
        velocidadeAlvoMs: Double
    ): Double {
        val cilindradaM3 = cilindradaUnitariaCc / 1_000_000.0
        val vazaoM3s = (cilindradaM3 * rpm) / (2.0 * 60.0)
        val areaM2 = vazaoM3s / velocidadeAlvoMs
        return sqrt(4.0 * areaM2 / PI) * 1000.0
    }

    /**
     * Comprimento de coletor por ressonância (estimativa):
     * L = (Cs / (RPM_alvo / 60 × 2)) × fator / 4
     * Cs = 343 m/s, fator 1 (1º harmônico) a 3 (3º harmônico)
     * Unidade final: mm
     */
    fun comprimentoColetor(
        rpmAlvo: Int,
        harmonico: Int = 3
    ): Double {
        val cs = 343.0
        val freq = rpmAlvo / 60.0 * 2.0
        val comprimentoM = (cs / freq) * harmonico / 4.0
        return comprimentoM * 1000.0
    }
}
