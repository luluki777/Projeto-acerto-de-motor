package com.exemplo.motortuner.core

/**
 * Parser numérico que aceita o formato brasileiro e o formato internacional.
 *
 * Regras:
 *  - "96,9"   -> 96.9
 *  - "96.9"   -> 96.9
 *  - "6.500"  -> 6500.0   (ponto tratado como separador de milhar)
 *  - "6,500"  -> 6.5      (vírgula tratada como decimal quando há 3 dígitos após? Não:
 *                          se houver apenas uma vírgula, tratamos como decimal.
 *                          Mas "6,500" seria ambíguo, então o app prioriza vírgula como decimal,
 *                          resultando em 6.5. Para milhar com vírgula, use ponto.)
 *  - "1.234,56" -> 1234.56
 *  - "1,234.56" -> 1234.56
 *  - "" -> null
 */
object Numeros {

    fun parse(texto: String?): Double? {
        if (texto.isNullOrBlank()) return null
        var s = texto.trim().replace(" ", "")

        // Remove separador de milhar quando claramente é milhar (ponto seguido de exatamente 3 dígitos)
        // Ex.: "6.500" -> "6500"
        val pontoMilhar = Regex("""\.(\d{3})(?=\.|,|$)""")
        val virgulaMilhar = Regex(""",(\d{3})(?=,|\.|$)""")

        val temPonto = s.contains('.')
        val temVirgula = s.contains(',')

        when {
            temPonto && temVirgula -> {
                // Formato misto: o último separador é o decimal
                val ultimoPonto = s.lastIndexOf('.')
                val ultimaVirgula = s.lastIndexOf(',')
                if (ultimoPonto > ultimaVirgula) {
                    // 1,234.56 -> ponto é decimal
                    s = s.replace(",", "")
                } else {
                    // 1.234,56 -> vírgula é decimal
                    s = s.replace(".", "").replace(",", ".")
                }
            }
            temPonto && !temVirgula -> {
                // Só ponto. Se casa como milhar (3 dígitos após), remove; senão mantém como decimal.
                s = if (pontoMilhar.containsMatchIn(s) && s.substringAfterLast('.').length == 3) {
                    s.replace(".", "")
                } else s
            }
            !temPonto && temVirgula -> {
                // Só vírgula: tratamos como decimal (padrão BR)
                s = s.replace(",", ".")
            }
        }

        return s.toDoubleOrNull()
    }

    fun parseInt(texto: String?): Int? {
        val d = parse(texto) ?: return null
        return if (d == d.toInt().toDouble()) d.toInt() else d.toInt()
    }

    /** Formata com vírgula decimal e ponto de milhar. */
    fun formatar(valor: Double, casas: Int = 2): String {
        val s = String.format("%,.${casas}f", valor)
        return s.replace(",", "X").replace(".", ",").replace("X", ".")
    }
}
