package com.pjcalc.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

const val REGIME_ALIQUOTAS = "ALIQUOTAS"
const val REGIME_MEI = "MEI"

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
