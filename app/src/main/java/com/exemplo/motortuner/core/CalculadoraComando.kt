package com.exemplo.motortuner.core

object CalculadoraComando {

    /** Overlap em graus. */
    fun overlap(duracaoAdm: Double, duracaoEsc: Double, lsa: Double): Double =
        (duracaoAdm + duracaoEsc) / 2.0 - 2.0 * lsa

    data class Eventos(
        val aberturaAdm: Double,   // BTDC (+ antes, − depois)
        val fechamentoAdm: Double, // ABDC
        val aberturaEsc: Double,   // BBDC
        val fechamentoEsc: Double  // ATDC
    )

    /** Eventos aproximados assumindo LSA simétrico. */
    fun eventos(duracaoAdm: Double, duracaoEsc: Double, lsa: Double): Eventos =
        Eventos(
            aberturaAdm = duracaoAdm / 2.0 - lsa,
            fechamentoAdm = duracaoAdm / 2.0 + lsa,
            aberturaEsc = duracaoEsc / 2.0 - lsa,
            fechamentoEsc = duracaoEsc / 2.0 + lsa
        )

    /** Levante efetivo considerando balancim. */
    fun levanteEfetivo(levanteCame: Double, relacaoBalancim: Double): Double =
        levanteCame * relacaoBalancim

    /**
     * Faixa de RPM sugerida (ESTIMATIVA) a partir da duração de admissão.
     * Base empírica: RPM_pico ≈ (duração_adm − 180) × 70 + 3000.
     * Ex.: duração 220° → 5800 rpm; duração 280° → 10000 rpm.
     */
    fun rpmPicoEstimado(duracaoAdm: Double): Int =
        ((duracaoAdm - 180.0) * 70.0 + 3000.0).toInt()

    /**
     * Classificação da duração.
     */
    fun classificacaoDuracao(duracao: Double): String = when {
        duracao < 200 -> "Suave / uso urbano"
        duracao < 240 -> "Uso misto"
        duracao < 270 -> "Esportivo"
        duracao < 290 -> "Competição / alta rotação"
        else -> "Corrida / pico muito alto"
    }
}
