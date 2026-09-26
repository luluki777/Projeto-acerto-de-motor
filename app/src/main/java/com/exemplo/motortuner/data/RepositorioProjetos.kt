package com.exemplo.motortuner.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class RepositorioProjetos(context: Context) {
    private val dao = AppDatabase.obter(context).projetoDao()

    fun listar(): Flow<List<Projeto>> = dao.listarTodos()

    suspend fun buscar(id: Long): Projeto? = dao.buscarPorId(id)

    suspend fun salvar(projeto: Projeto): Long {
        val comData = projeto.copy(alteradoEm = System.currentTimeMillis())
        return if (comData.id == 0L) {
            dao.inserir(comData)
        } else {
            dao.atualizar(comData)
            comData.id
        }
    }

    suspend fun duplicar(projeto: Projeto): Long {
        val copia = projeto.copy(
            id = 0,
            nome = "${projeto.nome} (cópia)",
            criadoEm = System.currentTimeMillis(),
            alteradoEm = System.currentTimeMillis()
        )
        return dao.inserir(copia)
    }

    suspend fun excluir(projeto: Projeto) = dao.excluir(projeto)
}
