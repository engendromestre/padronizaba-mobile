package com.padroniza.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.padroniza.app.data.local.dao.ConsultaDao
import com.padroniza.app.data.local.dao.DadosApoioDao
import com.padroniza.app.data.local.entities.CategoriaEntity
import com.padroniza.app.data.local.entities.ClienteEntity
import com.padroniza.app.data.local.entities.ConsultaEntity
import com.padroniza.app.data.local.entities.FazendaEntity
import com.padroniza.app.data.local.entities.FrigorificoEntity
import com.padroniza.app.data.local.entities.GrupoWhatsappEntity

@Database(
    entities = [
        ConsultaEntity::class,
        FrigorificoEntity::class,
        CategoriaEntity::class,
        ClienteEntity::class,
        FazendaEntity::class,
        GrupoWhatsappEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun consultaDao(): ConsultaDao
    abstract fun dadosApoioDao(): DadosApoioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "padroniza_offline.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
