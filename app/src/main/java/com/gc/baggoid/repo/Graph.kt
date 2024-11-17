package com.gc.baggoid.repo

import android.content.Context
import androidx.room.Room
import com.gc.baggoid.roomdb.CornHoleDB

//to init the db
object Graph {
    private lateinit var database: CornHoleDB

    val gameStateRepository by lazy {
        BaggoidRepository(gameStateDao = database.gameStateDao())
    }

    fun provide(context: Context){
        database = Room.databaseBuilder(context, CornHoleDB::class.java, "cornhole.db").build()
    }
}