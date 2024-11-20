package com.gc.baggoid.ui

import android.app.Application
import com.gc.baggoid.repo.Graph
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CornHoleApp:Application() {

    override fun onCreate() {
        super.onCreate()
//        Graph.provide(this)
    }

}