package com.tanjim.musicapp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // variables
    var startTime = 0.0
    var finalTime = 0.0
    var forwardTime = 10000
    var backwardTime = 10000
    var oneTimeOnly = 0

    // Handler
    var handler: Handler = Handler()

    // Media Player
    var mediaPlayer = MediaPlayer()
    lateinit var time_txt: TextView
    lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val play_btn: Button = findViewById(R.id.play_btn)
        val stop_btn: Button = findViewById(R.id.pause_btn)
        val forward_btn: Button = findViewById(R.id.forward_btn)
        val back_btn: Button = findViewById(R.id.back_btn)
        val pick_file_btn: Button = findViewById(R.id.pick_file_btn)

        val title_txt: TextView = findViewById(R.id.song_title)
        time_txt = findViewById(R.id.time_left_text)
        seekBar = findViewById(R.id.seek_bar)

        seekBar.isClickable = false

        // Adding Functionalities for the buttons
        play_btn.setOnClickListener {
            mediaPlayer.start()

            finalTime = mediaPlayer.duration.toDouble()
            startTime = mediaPlayer.currentPosition.toDouble()

            if (oneTimeOnly == 0) {
                seekBar.max = finalTime.toInt()
                oneTimeOnly = 1
            }

            time_txt.text = startTime.toString()
            seekBar.setProgress(startTime.toInt())

            handler.postDelayed(UpdateSongTime, 100)
        }

        // Stop Button
        stop_btn.setOnClickListener {
            mediaPlayer.pause()
        }

        // Forward Button
        forward_btn.setOnClickListener {
            var temp = startTime
            if ((temp + forwardTime) <= finalTime) {
                startTime = startTime + forwardTime
                mediaPlayer.seekTo(startTime.toInt())
            } else {
                Toast.makeText(this, "Can't Jump forward", Toast.LENGTH_LONG).show()
            }
        }

        back_btn.setOnClickListener {
            var temp = startTime.toInt()

            if ((temp - backwardTime) > 0) {
                startTime = startTime - backwardTime
                mediaPlayer.seekTo(startTime.toInt())
            } else {
                Toast.makeText(this, "Can't Jump backward", Toast.LENGTH_LONG).show()
            }
        }

        // Pick File Button
        pick_file_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val selectedAudioUri: Uri? = data.data
            mediaPlayer = MediaPlayer.create(this, selectedAudioUri)
            mediaPlayer.start()

            finalTime = mediaPlayer.duration.toDouble()
            startTime = mediaPlayer.currentPosition.toDouble()

            if (oneTimeOnly == 0) {
                seekBar.max = finalTime.toInt()
                oneTimeOnly = 1
            }

            time_txt.text = startTime.toString()
            seekBar.setProgress(startTime.toInt())

            handler.postDelayed(UpdateSongTime, 100)
        }
    }

    // Creating the Runnable
    val UpdateSongTime: Runnable = object : Runnable {
        override fun run() {
            startTime = mediaPlayer.currentPosition.toDouble()
            time_txt.text = "" +
                    String.format(
                        "%d min , %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(
                            startTime.toLong()
                                    - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    startTime.toLong()
                                )
                            ))
                    )

            seekBar.progress = startTime.toInt()
            handler.postDelayed(this, 100)
        }
    }
}
