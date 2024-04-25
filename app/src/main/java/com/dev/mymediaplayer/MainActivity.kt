package com.dev.mymediaplayer

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var mMediaPlayer: MediaPlayer? = null
    private var isReady: Boolean = false
    private var audioUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPlay = findViewById<Button>(R.id.btn_play)
        val btnPause = findViewById<Button>(R.id.btn_pause)
        val btnStop = findViewById<Button>(R.id.btn_stop)
        val btnSelectAudio = findViewById<Button>(R.id.btn_select_audio)

        btnSelectAudio.setOnClickListener {
            selectAudioFile()
        }

        btnPlay.setOnClickListener {
            if (audioUri != null) {
                if (!isReady) {
                    mMediaPlayer?.prepareAsync()
                } else {
                    if (mMediaPlayer?.isPlaying == true) {
                        mMediaPlayer?.pause()
                    } else {
                        mMediaPlayer?.start()
                    }
                }
            }
        }

        btnPause.setOnClickListener {
            if (mMediaPlayer?.isPlaying == true) {
                mMediaPlayer?.pause()
            }
        }

        btnStop.setOnClickListener {
            if (mMediaPlayer?.isPlaying == true || isReady) {
                mMediaPlayer?.stop()
                isReady = false
            }
        }
    }

    private fun selectAudioFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            audioUri = result.data?.data
            initMediaPlayer(audioUri)
        }
    }

    private fun initMediaPlayer(uri: Uri?) {
        mMediaPlayer?.release()
        mMediaPlayer = null
        mMediaPlayer = MediaPlayer()

        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        mMediaPlayer?.setAudioAttributes(attribute)

        try {
            mMediaPlayer?.setDataSource(applicationContext, uri!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mMediaPlayer?.setOnPreparedListener {
            isReady = true
        }

        mMediaPlayer?.setOnErrorListener { _, _, _ -> false }
        mMediaPlayer?.prepareAsync()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaPlayer?.release()
    }
}