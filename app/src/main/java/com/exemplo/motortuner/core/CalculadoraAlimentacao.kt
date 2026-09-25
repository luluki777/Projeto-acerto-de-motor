package com.exemplo.motortuner.core

import kotlin.math.sqrt

object CalculadoraAlimentacao {

    /**
     * Diâmetro sugerido do venturi do carburador (mm).
     * ESTIMATIVA baseada em regra prática de preparação:
     *   D = sqrt(cilindrada_unitária_cc × RPM / 1000) × k
     * onde k é 0.65 (uso geral) a 0.80 (alta performance).
     *
     * A referência clássica é D_venturi ≈ 0.80 a 0.85 × D_válvula_admissão.
     * Como D_válvula não é entrada obrigatória aqui, usamos a regra de cilindrada × RPM.
     */
    fun venturiSugerido(
        cilindradaUnitariaCc: Double,
        rpmMax: Int,
        altaPerformance: Boolean
    ): Double {
        val k = if (altaPerformance) 0.80 else 0.65
        return sqrt(cilindradaUnitariaCc * rpmMax / 1000.0) * k
    }

    /** Faixa (mín, máx) em torno do sugerido. */
    fun faixaVenturi(sugerido: Double): Pair<Double, Double> =
        Pair(sugerido * 0.92, sugerido * 1.08)

    /**
     * Vazão de ar estimada em m³/h.
     * Q = cilindrada_total_L × (RPM / 2) × VE × 60
     * VE típico: 0.80 (aspirado comum) a 1.00 (bem preparado).
     */
    fun vazaoAr(
        cilindradaTotalL: Double,
        rpmMax: Int,
        ve: Double
    ): Double = cilindradaTotalL * (rpmMax / 2.0) * ve * 60.0

    /**
     * Vazão de ar em kg/h, considerando densidade padrão 1.184 kg/m³ (20°C, 1 atm).
     */
    fun vazaoArKgH(vazaoM3H: Double): Double = vazaoM3H * 1.184

    /**
     * Faixa inicial de giclê principal (apenas ESTIMATIVA).
     * A regra abaixo é heurística e varia muito com carburador, agulha, gicleur de marcha lenta,
     * altitude, temperatura e projeto. Serve APENAS como ponto de partida.
     *
     * Ponto de partida: giclê ≈ √(cilindrada_unitária_cc) × fator
     * fator 1.8 a 2.2 dependendo do tipo de motor.
     */
    fun faixaGiclePrincipal(cilindradaUnitariaCc: Double): Pair<Double, Double> {
        val base = sqrt(cilindradaUnitariaCc)
        return Pair(base * 1.8, base * 2.2)
    }

    data class ResultadoAlimentacao(
        val cilindradaUnitariaCc: Double,
        val cilindradaTotalCc: Double,
        val venturiSugeridoMm: Double,
        val venturiMinMm: Double,
        val venturiMaxMm: Double,
        val vazaoArM3H: Double,
        val vazaoArKgH: Double,
        val gicleMin: Double?,
        val gicleMax: Double?
    )

    fun calcular(
        boreMm: Double, strokeMm: Double, cilindros: Int,
        rpmMax: Int, ve: Double, altaPerformance: Boolean
    ): ResultadoAlimentacao {
        val unit = CalculadoraCompressao.volumeVarrido(boreMm, strokeMm)
        val total = unit * cilindros
        val vent = venturiSugerido(unit, rpmMax, altaPerformance)
        val (vMin, vMax) = faixaVenturi(vent)
        val q = vazaoAr(total / 1000.0, rpmMax, ve)
        val (gMin, gMax) = faixaGiclePrincipal(unit)
        return ResultadoAlimentacao(
            cilindradaUnitariaCc = unit,
            cilindradaTotalCc = total,
            venturiSugeridoMm = vent,
            venturiMinMm = vMin,
            venturiMaxMm = vMax,
            vazaoArM3H = q,
            vazaoArKgH = vazaoArKgH(q),
            gicleMin = gMin,
            gicleMax = gMax
        )
    }
}
