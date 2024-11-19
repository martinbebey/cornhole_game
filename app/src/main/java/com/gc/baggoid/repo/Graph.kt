package com.gc.baggoid.repo
import android.content.Context
import androidx.room.Room
import com.gc.baggoid.R
import com.gc.baggoid.roomdb.CornHoleDB

//To init the db
object Graph {

    private lateinit var database: CornHoleDB

    fun provide(context: Context){
        database = Room.databaseBuilder(context, CornHoleDB::class.java, "cornhole.db").build()
    }
}