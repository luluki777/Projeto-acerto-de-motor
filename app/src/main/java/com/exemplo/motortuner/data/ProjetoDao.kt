package com.exemplo.motortuner.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjetoDao {

    @Query("SELECT * FROM projetos ORDER BY alteradoEm DESC")
    fun listarTodos(): Flow<List<Projeto>>

    @Query("SELECT * FROM projetos WHERE id = :id")
    suspend fun buscarPorId(id: Long): Projeto?

    @Insert
    suspend fun inserir(projeto: Projeto): Long

    @Update
    suspend fun atualizar(projeto: Projeto)

    @Delete
    suspend fun excluir(projeto: Projeto)

    @Query("DELETE FROM projetos")
    suspend fun excluirTodos()
}
