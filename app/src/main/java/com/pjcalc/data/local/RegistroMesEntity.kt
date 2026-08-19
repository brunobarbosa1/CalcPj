package com.pjcalc.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Discriminadores gravados no banco. Independentes do rótulo exibido na tela. */
const val REGIME_ALIQUOTAS = "ALIQUOTAS"
const val REGIME_MEI = "MEI"

/**
 * O regime é achatado em colunas anuláveis: só as do regime gravado vêm
 * preenchidas. Um regime novo no futuro é mais uma coluna, sem serialização.
 */
@Entity(
    tableName = "registro_mes",
    indices = [Index(value = ["ano", "mes"], unique = true)]
)
data class RegistroMesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ano: Int,
    val mes: Int,
    val horas: Double,
    val valorHora: Double,
    val regime: String,
    val aliqINSS: Double?,
    val aliqISS: Double?,
    val aliqIRRF: Double?,
    val das: Double?,
    val criadoEm: Long
)
