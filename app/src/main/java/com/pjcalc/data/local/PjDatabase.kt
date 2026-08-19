package com.pjcalc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RegistroMesEntity::class], version = 2, exportSchema = false)
abstract class PjDatabase : RoomDatabase() {
    abstract fun registroMesDao(): RegistroMesDao
}
