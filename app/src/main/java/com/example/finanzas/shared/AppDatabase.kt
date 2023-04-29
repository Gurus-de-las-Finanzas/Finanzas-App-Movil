package com.example.finanzas.shared

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.finanzas.security.models.LoginCredentials
import com.example.finanzas.security.persistence.LoginCredentialsDAO

@Database(entities = [LoginCredentials::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getLoginCredentialsDao(): LoginCredentialsDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                    .databaseBuilder(context, AppDatabase::class.java, "finanzas.db")
                    .allowMainThreadQueries()
                    .build()
            }

            return INSTANCE as AppDatabase
        }
    }
}