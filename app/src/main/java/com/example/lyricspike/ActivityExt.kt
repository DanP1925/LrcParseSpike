package com.example.lyricspike

import android.app.Activity

fun Activity.getViewModelFactory(): ViewModelFactory {
    val repository = (this.application as TestApplication).lrcRepository
    return ViewModelFactory(repository)
}