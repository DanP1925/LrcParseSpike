package com.example.lyricspike

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ViewModelFactory constructor(
        private val lrcRepository: LrcRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            with(modelClass) {
                when {
                    isAssignableFrom(MainViewModel::class.java) ->
                        MainViewModel(lrcRepository)
                    else ->
                        throw IllegalArgumentException("Unknown viewmodel class")
                }
            } as T

}