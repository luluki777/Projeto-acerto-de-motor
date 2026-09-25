package com.exemplo.motortuner.core

object CalculadoraIgnicao {

    data class PontoCurva(val rpm: Int, val avanco: Double)

    /**
     * Gera uma curva base de avanço a partir de 4 parâmetros.
     * Retorna 7 pontos entre rpmInicial e rpmFinal.
     * A curva é uma interpolação linear entre avancoInicial e avancoMaximo.
     */
    fun curvaBase(
        rpmInicial: Int,
        avancoInicial: Double,
        rpmMaxAvanco: Int,
        avancoMaximo: Double,
        rpmFinal: Int = 7000
    ): List<PontoCurva> {
        val passos = 6
        val passoRpm = (rpmFinal - rpmInicial) / passos
        return (0..passos).map { i ->
            val rpm = rpmInicial + passoRpm * i
            val avanco = when {
                rpm <= rpmMaxAvanco -> {
                    val frac = (rpm - rpmInicial).toDouble() / (rpmMaxAvanco - rpmInicial)
                    avancoInicial + (avancoMaximo - avancoInicial) * frac.coerceIn(0.0, 1.0)
                }
                else -> {
                    // Após o pico, avanço cai levemente (proteção)
                    val queda = (rpm - rpmMaxAvanco) / 1000.0 * 1.0
                    (avancoMaximo - queda).coerceAtLeast(avancoMaximo - 6.0)
                }
            }
            PontoCurva(rpm, avanco)
        }
    }

    /** Interpolação linear para obter avanço em qualquer RPM. */
    fun interpolar(curva: List<PontoCurva>, rpm: Int): Double {
        if (curva.isEmpty()) return 0.0
        if (rpm <= curva.first().rpm) return curva.first().avanco
        if (rpm >= curva.last().rpm) return curva.last().avanco
        for (i in 0 until curva.size - 1) {
            val a = curva[i]
            val b = curva[i + 1]
            if (rpm in a.rpm..b.rpm) {
                val frac = (rpm - a.rpm).toDouble() / (b.rpm - a.rpm)
                return a.avanco + (b.avanco - a.avanco) * frac
            }
        }
        return curva.last().avanco
    }

    /** Riscos conforme taxa de compressão e combustível (orientativo). */
    fun alertaCombustivel(combustivel: String, taxa: Double?): String? {
        if (taxa == null) return null
        return when {
            combustivel.contains("Gasolina", true) && taxa > 11.5 ->
                "Gasolina comum acima de 11,5:1 exige gasolina premium e/ou avanço conservador."
            combustivel.contains("Etanol", true) && taxa > 14.5 ->
                "Etanol acima de 14,5:1 exige atenção à temperatura e ponto."
            else -> null
        }
    }
}
