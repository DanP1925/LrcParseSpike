package com.example.lyricspike

import android.app.Application

class TestApplication : Application() {

    val lrcRepository : LrcRepository
        get() = ServiceLocator.provideLrcRepository(this)

}