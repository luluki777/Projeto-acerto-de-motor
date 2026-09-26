package com.exemplo.motortuner.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projetos")
data class Projeto(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val descricao: String = "",
    // Motor
    val boreMm: Double? = null,
    val strokeMm: Double? = null,
    val cilindros: Int? = null,
    val rpmMax: Int? = null,
    val volumeCamaraCc: Double? = null,
    val volumePistaoCc: Double? = null,
    val alturaDeckMm: Double? = null,
    val espessuraJuntaMm: Double? = null,
    val diametroJuntaMm: Double? = null,
    // Compressão
    val taxaCompressao: Double? = null,
    // Alimentação
    val venturiSugeridoMm: Double? = null,
    // Injeção
    val potenciaHp: Double? = null,
    val bsfc: Double? = null,
    val vazaoBicoCcMin: Double? = null,
    // Desempenho
    val torqueNm: Double? = null,
    val bmepBar: Double? = null,
    // Metadados
    val criadoEm: Long = System.currentTimeMillis(),
    val alteradoEm: Long = System.currentTimeMillis()
)
