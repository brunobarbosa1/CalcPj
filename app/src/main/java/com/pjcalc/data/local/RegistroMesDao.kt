package com.pjcalc.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroMesDao {
    @Query("SELECT * FROM registro_mes ORDER BY ano DESC, mes DESC, criadoEm DESC")
    fun observarTodos(): Flow<List<RegistroMesEntity>>

    @Query("SELECT * FROM registro_mes WHERE id = :id")
    suspend fun porId(id: Long): RegistroMesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(registro: RegistroMesEntity): Long
}
