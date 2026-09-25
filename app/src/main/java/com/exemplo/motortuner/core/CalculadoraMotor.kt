package com.exemplo.motortuner.core

import kotlin.math.PI

data class DetalheCalculo(
    val titulo: String,
    val formula: String,
    val valores: String,
    val resultado: String,
    val tipo: TipoResultado
)

enum class TipoResultado { CALCULADO, ESTIMADO }

data class EntradaMotor(
    val boreMm: Double,
    val strokeMm: Double,
    val cilindros: Int,
    val rpmMax: Int?,
    val volumeCamaraCc: Double?,
    val volumePistaoCc: Double?,
    val alturaDeckMm: Double?,
    val espessuraJuntaMm: Double?,
    val diametroJuntaMm: Double?
)

object CalculadoraMotor {

    /** Cilindrada unitária em cc. */
    fun cilindradaUnitaria(boreMm: Double, strokeMm: Double): Double =
        PI / 4.0 * boreMm * boreMm * strokeMm / 1000.0

    fun cilindradaTotal(boreMm: Double, strokeMm: Double, cilindros: Int): Double =
        cilindradaUnitaria(boreMm, strokeMm) * cilindros

    fun relacaoBoreStroke(boreMm: Double, strokeMm: Double): Double =
        boreMm / strokeMm

    fun velocidadeMediaPistao(strokeMm: Double, rpm: Int): Double =
        2.0 * strokeMm * rpm / 60000.0

    /** Volume da junta em cc (considerando geometria circular). */
    fun volumeJunta(diametroJuntaMm: Double, espessuraJuntaMm: Double): Double =
        PI / 4.0 * diametroJuntaMm * diametroJuntaMm * espessuraJuntaMm / 1000.0

    /** Volume acima do pistão no PMS (deck + junta + câmara + volume do pistão). */
    fun volumeAcimaPistao(
        volumeCamaraCc: Double?,
        volumeJuntaCc: Double?,
        deckCc: Double?,
        volumePistaoCc: Double?
    ): Double? {
        val partes = listOfNotNull(volumeCamaraCc, volumeJuntaCc, deckCc, volumePistaoCc)
        return if (partes.isEmpty()) null else partes.sum()
    }

    /** Volume do deck em cc — depende do deck (mm) e do bore. */
    fun volumeDeck(boreMm: Double, alturaDeckMm: Double): Double =
        PI / 4.0 * boreMm * boreMm * alturaDeckMm / 1000.0

    fun detalhar(e: EntradaMotor): List<DetalheCalculo> {
        val lista = mutableListOf<DetalheCalculo>()

        val vUnit = cilindradaUnitaria(e.boreMm, e.strokeMm)
        lista += DetalheCalculo(
            titulo = "Cilindrada unitária",
            formula = "π × (D² / 4) × curso / 1000",
            valores = "D = ${e.boreMm} mm, curso = ${e.strokeMm} mm",
            resultado = "${Numeros.formatar(vUnit, 2)} cc",
            tipo = TipoResultado.CALCULADO
        )

        val vTotal = cilindradaTotal(e.boreMm, e.strokeMm, e.cilindros)
        lista += DetalheCalculo(
            titulo = "Cilindrada total",
            formula = "cilindrada unitária × nº cilindros",
            valores = "${Numeros.formatar(vUnit, 2)} cc × ${e.cilindros}",
            resultado = "${Numeros.formatar(vTotal, 2)} cc",
            tipo = TipoResultado.CALCULADO
        )

        val rb = relacaoBoreStroke(e.boreMm, e.strokeMm)
        lista += DetalheCalculo(
            titulo = "Relação diâmetro/curso",
            formula = "diâmetro / curso",
            valores = "${e.boreMm} / ${e.strokeMm}",
            resultado = Numeros.formatar(rb, 3),
            tipo = TipoResultado.CALCULADO
        )

        if (e.rpmMax != null && e.rpmMax > 0) {
            val vp = velocidadeMediaPistao(e.strokeMm, e.rpmMax)
            lista += DetalheCalculo(
                titulo = "Velocidade média do pistão",
                formula = "2 × curso × RPM / 60000",
                valores = "curso = ${e.strokeMm} mm, RPM = ${e.rpmMax}",
                resultado = "${Numeros.formatar(vp, 2)} m/s",
                tipo = TipoResultado.CALCULADO
            )
        }

        if (e.diametroJuntaMm != null && e.espessuraJuntaMm != null) {
            val vj = volumeJunta(e.diametroJuntaMm, e.espessuraJuntaMm)
            lista += DetalheCalculo(
                titulo = "Volume da junta",
                formula = "π × (Dj² / 4) × espessura / 1000",
                valores = "Dj = ${e.diametroJuntaMm} mm, esp = ${e.espessuraJuntaMm} mm",
                resultado = "${Numeros.formatar(vj, 3)} cc",
                tipo = TipoResultado.CALCULADO
            )
        }

        if (e.alturaDeckMm != null) {
            val vd = volumeDeck(e.boreMm, e.alturaDeckMm)
            lista += DetalheCalculo(
                titulo = "Volume do deck",
                formula = "π × (D² / 4) × altura do deck / 1000",
                valores = "D = ${e.boreMm} mm, deck = ${e.alturaDeckMm} mm",
                resultado = "${Numeros.formatar(vd, 3)} cc",
                tipo = TipoResultado.CALCULADO
            )
        }

        val vAcima = volumeAcimaPistao(
            e.volumeCamaraCc,
            if (e.diametroJuntaMm != null && e.espessuraJuntaMm != null)
                volumeJunta(e.diametroJuntaMm, e.espessuraJuntaMm) else null,
            if (e.alturaDeckMm != null) volumeDeck(e.boreMm, e.alturaDeckMm) else null,
            e.volumePistaoCc
        )
        if (vAcima != null) {
            lista += DetalheCalculo(
                titulo = "Volume total acima do pistão",
                formula = "soma de câmara + junta + deck + volume do pistão",
                valores = "componentes fornecidos",
                resultado = "${Numeros.formatar(vAcima, 3)} cc",
                tipo = TipoResultado.CALCULADO
            )
        }

        return lista
    }
}
