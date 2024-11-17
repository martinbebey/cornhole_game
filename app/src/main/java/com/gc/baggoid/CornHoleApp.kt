package com.gc.baggoid

import android.app.Application
import com.gc.baggoid.repo.Graph

class CornHoleApp:Application() {

    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}