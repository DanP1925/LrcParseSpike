package com.example.lyricspike

import android.content.Context

object ServiceLocator {

    @Volatile
    var lrcRepository: LrcRepository? = null

    fun provideLrcRepository(context: Context) : LrcRepository {
        synchronized(this){
            return lrcRepository ?: lrcRepository ?: createLrcRepository(context)
        }
    }

    private fun createLrcRepository(context: Context) : LrcRepository {
        return LrcRepository()
    }

}