package com.exemplo.motortuner.core

import kotlin.math.PI

object CalculadoraCompressao {

    /** Volume varrido (cilindrada unitária) em cc. */
    fun volumeVarrido(boreMm: Double, strokeMm: Double): Double =
        PI / 4.0 * boreMm * boreMm * strokeMm / 1000.0

    /** Volume residual acima do pistão no PMS, em cc. */
    fun volumeResidual(
        volumeCamaraCc: Double,
        volumeJuntaCc: Double,
        volumeDeckCc: Double,
        volumePistaoCc: Double
    ): Double = volumeCamaraCc + volumeJuntaCc + volumeDeckCc + volumePistaoCc

    /** Volume da junta (geometria circular) em cc. */
    fun volumeJunta(diametroMm: Double, espessuraMm: Double): Double =
        PI / 4.0 * diametroMm * diametroMm * espessuraMm / 1000.0

    /** Volume do deck em cc. */
    fun volumeDeck(boreMm: Double, alturaMm: Double): Double =
        PI / 4.0 * boreMm * boreMm * alturaMm / 1000.0

    /**
     * Taxa de compressão estática.
     * CR = (Vd + Vc) / Vc
     */
    fun taxaCompressao(volumeVarridoCc: Double, volumeResidualCc: Double): Double {
        require(volumeResidualCc > 0) { "Volume residual deve ser maior que 0." }
        return (volumeVarridoCc + volumeResidualCc) / volumeResidualCc
    }

    /**
     * Simulação: qual a nova taxa se eu trocar a junta?
     * Recalcula apenas o volume da junta, mantendo os demais volumes fixos.
     */
    fun simularJunta(
        volumeVarridoCc: Double,
        volumeCamaraCc: Double,
        volumeDeckCc: Double,
        volumePistaoCc: Double,
        diametroJuntaMm: Double,
        novaEspessuraJuntaMm: Double
    ): Double {
        val novaJunta = volumeJunta(diametroJuntaMm, novaEspessuraJuntaMm)
        val vc = volumeCamaraCc + novaJunta + volumeDeckCc + volumePistaoCc
        return taxaCompressao(volumeVarridoCc, vc)
    }

    /**
     * Simulação: qual a nova taxa se eu alterar a câmara?
     */
    fun simularCamara(
        volumeVarridoCc: Double,
        volumeJuntaCc: Double,
        volumeDeckCc: Double,
        volumePistaoCc: Double,
        novaCamaraCc: Double
    ): Double {
        val vc = novaCamaraCc + volumeJuntaCc + volumeDeckCc + volumePistaoCc
        return taxaCompressao(volumeVarridoCc, vc)
    }

    /** Volume residual necessário para atingir uma taxa desejada. */
    fun volumeResidualParaTaxa(volumeVarridoCc: Double, taxaDesejada: Double): Double {
        require(taxaDesejada > 1.0)
        return volumeVarridoCc / (taxaDesejada - 1.0)
    }
}
