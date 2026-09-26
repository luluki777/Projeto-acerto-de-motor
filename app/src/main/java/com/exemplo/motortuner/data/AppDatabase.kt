package com.exemplo.motortuner.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Projeto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projetoDao(): ProjetoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obter(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "motor_tuner.db"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}
