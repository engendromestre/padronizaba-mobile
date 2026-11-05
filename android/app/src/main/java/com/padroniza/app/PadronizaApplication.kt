package com.padroniza.app

import android.app.Application
import com.padroniza.app.worker.SyncManager

class PadronizaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Agenda a sincronização periódica resiliente em segundo plano
        SyncManager.agendarSincronizacaoPeriodica(this)
    }
}
