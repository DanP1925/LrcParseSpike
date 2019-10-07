package com.example.lyricspike

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.*
import java.lang.Exception

class LrcRepository {

    suspend fun getLyrics(file: File, lrcUrl: String): LrcFile? {

        return withContext(Dispatchers.IO) {
            try {
                var lrcFile: LrcFile? = null
                val service: LrcInterface? = RestClientInstance.retrofitInstance?.create(LrcInterface::class.java)
                val response = service?.getSong(lrcUrl)
                val hasLyricsBeenWritten = writeResponseToDisk(file, response!!)
                if (hasLyricsBeenWritten) {
                    lrcFile = LrcFile(file)
                }
                return@withContext lrcFile
            } catch (exception: Exception) {
                Log.e("RetroTest", "Mal")
            }
            return@withContext null
        }
    }

    private fun writeResponseToDisk(file: File, body: ResponseBody): Boolean {
        try {

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null

            try {
                val fileReader = ByteArray(4096)
                val fileSize: Long = body.contentLength()
                var fileSizeDownloaded = 0

                inputStream = body.byteStream()
                outputStream = FileOutputStream(file)

                while (true) {
                    val read = inputStream.read(fileReader)

                    if (read == -1) {
                        break
                    }

                    outputStream.write(fileReader, 0, read)

                    fileSizeDownloaded += read

                    Log.e("Downloading", "$fileSizeDownloaded of $fileSize")
                }

                outputStream.flush()

                return true
            } catch (exception: IOException) {
                return false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (exception: IOException) {
            return false
        }
    }

}