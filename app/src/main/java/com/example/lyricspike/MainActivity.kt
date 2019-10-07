package com.example.lyricspike

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import java.io.File

class MainActivity : AppCompatActivity() {


    private val viewModel by viewModels<MainViewModel> { getViewModelFactory() }

    var mediaPlayer: MediaPlayer? = null
    lateinit var prevText: TextView
    lateinit var highlightText: TextView
    lateinit var nextText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val path = "${externalCacheDir.absolutePath}/lyricFile.lrc"
        val file: File = File(path)

        val songObserver = Observer<Song> {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(it.mp3)
                prepare()
            }
        }
        viewModel.song.observe(this, songObserver)

        prevText = findViewById(R.id.prev_lyrics)
        highlightText = findViewById(R.id.highlighted_lyrics)
        nextText = findViewById(R.id.next_lyrics)

        val lrcFileObserver = Observer<LrcFile> {
            prevText.text = ""
            var rowIndex = 0
            highlightText.text = it.rows[rowIndex].fullSentence()
            rowIndex++
            nextText.text = it.rows[rowIndex].fullSentence()

            if (mediaPlayer != null) {
                mediaPlayer!!.start()

                var wordIndex = 0

                object : CountDownTimer(it.timeLength.toLong(), 10) {

                    override fun onFinish() {
                        Log.e("TimerTesting", "Time ended")
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        if (isNextWordTime(it, rowIndex, wordIndex, millisUntilFinished)) {
                            changeToNextWord(it.rows[rowIndex], wordIndex)
                            wordIndex++
                        }

                        if (isNextRowTime(it, rowIndex, millisUntilFinished)) {
                            changeToNextRow(it, rowIndex)
                            rowIndex++
                            wordIndex = 0
                        }
                    }
                }.start()
            }
        }
        viewModel.lrcFile.observe(this, lrcFileObserver)

        viewModel.start(file)
    }

    private fun isNextWordTime(lrcFile: LrcFile, rowIndex: Int, wordIndex: Int, millisUntilFinished: Long): Boolean {
        if (wordIndex == lrcFile.rows[rowIndex].words.size) return false
        Log.e("TimerTesting", "Row index $rowIndex")
        Log.e("TimerTesting", "Word index $wordIndex")
        Log.e("TimerTesting", "Evaluate startTime ${lrcFile.rows[rowIndex].startTime}")
        val currentTime = lrcFile.timeLength - millisUntilFinished
        val nextWordTime = lrcFile.rows[rowIndex].words[wordIndex].endTime.toLong()
        Log.e("TimerTesting", "Evaluate current $currentTime")
        Log.e("TimerTesting", "Evaluate nextword $nextWordTime")
        Log.e("TimerTesting", "Evaluate endTime ${lrcFile.rows[rowIndex + 1].startTime}")
        Log.e("TimerTesting", "Before result ${currentTime > nextWordTime}")
        return currentTime >= nextWordTime
    }

    private fun changeToNextWord(lrcRow: LrcRow, wordIndex: Int) {
        var coloredSentence = ""
        lrcRow.words.forEachIndexed { index, lrcWord ->
            coloredSentence += lrcWord.word + " "
            if (index > wordIndex) {
                return@forEachIndexed
            }
        }
        val spannableString = SpannableString(lrcRow.fullSentence())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spannableString.setSpan(
                    ForegroundColorSpan(getColor(R.color.colorPrimaryDark)),
                    0,
                    coloredSentence.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else {
            spannableString.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorPrimaryDark)),
                    0,
                    coloredSentence.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        Log.e("TimerTesting", "Colored sentence $coloredSentence")
        highlightText.text = spannableString
    }

    private fun isNextRowTime(lrcFile: LrcFile, rowIndex: Int, millisUntilFinished: Long) =
            lrcFile.timeLength - millisUntilFinished >= lrcFile.rows[rowIndex + 1].startTime.toLong()

    private fun changeToNextRow(lrcFile: LrcFile, index: Int) {
        prevText.text = lrcFile.rows[index - 1].fullSentence()
        highlightText.text = lrcFile.rows[index].fullSentence()
        nextText.text = lrcFile.rows[index + 1].fullSentence()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
