package com.example.lyricspike

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(
        private val lrcRepository: LrcRepository
) : ViewModel() {

    private val _lrcFile = MutableLiveData<LrcFile>()
    val lrcFile: LiveData<LrcFile> = _lrcFile

    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> = _song

    fun start(file: File) {

        _song.value = Song(
                "Soda Stereo",
                "Persiana Americana",
                "https://ks-videos-prod.s3.amazonaws.com/277/277.mp3?AWSAccessKeyId=AKIAJKNSOOAN4I3YXIMA&Signature=IJh6mv6yyOW%2BhVDlqUgUfrEWWh4%3D&Expires=2091336774",
                "https://ks-videos-prod.s3.amazonaws.com/277/277.lrc?AWSAccessKeyId=AKIAJKNSOOAN4I3YXIMA&Signature=qsWFZqSHGfJftGy3MH7yb5DgGtk%3D&Expires=2091336744"
        )


        viewModelScope.launch {
            _lrcFile.value = lrcRepository.getLyrics(file, song.value!!.lyric)!!
        }
    }

}