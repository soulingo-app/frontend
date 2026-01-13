package com.soulingo.app.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(): String? {
        return try {
            // Eski dosyayı temizle
            stopRecording()

            // Yeni dosya oluştur
            val timestamp = System.currentTimeMillis()
            outputFile = File(context.cacheDir, "voice_${timestamp}.m4a")

            Log.d("AudioRecorder", "Output file: ${outputFile?.absolutePath}")

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile?.absolutePath)

                prepare()
                start()

                Log.d("AudioRecorder", "Recording started successfully")
            }

            outputFile?.absolutePath
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Start recording failed", e)
            mediaRecorder?.release()
            mediaRecorder = null
            null
        }
    }

    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                try {
                    stop()
                    Log.d("AudioRecorder", "Recording stopped")
                } catch (e: Exception) {
                    Log.e("AudioRecorder", "Stop failed", e)
                }
                release()
            }
            mediaRecorder = null

            val path = outputFile?.absolutePath
            Log.d("AudioRecorder", "Final file: $path, exists: ${outputFile?.exists()}")
            path
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Stop recording error", e)
            null
        }
    }

    fun release() {
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            Log.e("AudioRecorder", "Release error", e)
        }
        mediaRecorder = null
    }
}