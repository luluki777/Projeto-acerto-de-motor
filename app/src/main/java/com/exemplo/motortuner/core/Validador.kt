package com.exemplo.motortuner.core

data class ResultadoValidacao(
    val valido: Boolean,
    val erros: Map<String, String> = emptyMap()
) {
    fun mensagem(): String? = erros.values.firstOrNull()
}

object Validador {
    fun validarMotor(
        bore: Double?, stroke: Double?, cilindros: Int?,
        rpm: Int?, volumeCamara: Double?
    ): ResultadoValidacao {
        val erros = linkedMapOf<String, String>()

        if (bore == null) erros["bore"] = "Diâmetro do pistão é obrigatório."
        else if (bore <= 0) erros["bore"] = "Diâmetro do pistão deve ser maior que 0."
        else if (bore > 300) erros["bore"] = "Diâmetro do pistão acima de 300 mm parece inválido."

        if (stroke == null) erros["stroke"] = "Curso é obrigatório."
        else if (stroke <= 0) erros["stroke"] = "Curso deve ser maior que 0."
        else if (stroke > 300) erros["stroke"] = "Curso acima de 300 mm parece inválido."

        if (cilindros == null) erros["cilindros"] = "Número de cilindros é obrigatório."
        else if (cilindros !in 1..16) erros["cilindros"] = "Número de cilindros deve estar entre 1 e 16."

        if (rpm != null && rpm < 0) erros["rpm"] = "RPM não pode ser negativo."

        if (volumeCamara != null && volumeCamara < 0)
            erros["camara"] = "Volume da câmara não pode ser negativo."

        return ResultadoValidacao(erros.isEmpty(), erros)
    }
}
