package com.example.voicerecorderpoc

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import java.io.IOException

private const val REQUEST_AUDIO_PERMISSION_CODE = 1
private const val LOG_TAG = "AudioRecording"

class MainActivity : AppCompatActivity() {
    private var startbtn: Button? = null

    private var playbtn: Button? = null
    private var stopplay: Button? = null
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var mFileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startbtn = findViewById<View>(R.id.btnRecord) as Button
        playbtn = findViewById<View>(R.id.btnPlay) as Button
        stopplay = findViewById<View>(R.id.btnStopPlay) as Button

        playbtn?.isEnabled = false
        stopplay?.isEnabled = false
        mFileName = this.getExternalFilesDir(null)!!.absolutePath
        mFileName += "/AudioRecording.3gp"
        startbtn?.setOnClickListener {
            if (checkPermissions()) {
                supportFragmentManager.beginTransaction()
                supportFragmentManager.commit {
                    replace(R.id.fragment_container_view, RecordingFragment())
                    addToBackStack(null)
                }
            } else {
                requestPermissions()
            }
        }

        playbtn?.setOnClickListener {
            startbtn!!.isEnabled = true
            playbtn?.isEnabled = false
            stopplay?.isEnabled = true
            mPlayer = MediaPlayer()
            try {
                mPlayer?.setDataSource(mFileName)
                mPlayer?.prepare()
                mPlayer?.start()
                Toast.makeText(
                    applicationContext,
                    "Recording Started Playing",
                    Toast.LENGTH_LONG
                ).show()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
        stopplay?.setOnClickListener {
            mPlayer?.release()
            mPlayer = null
            startbtn!!.isEnabled = true
            playbtn?.isEnabled = true
            stopplay?.isEnabled = false
            Toast.makeText(applicationContext, "Playing Audio Stopped", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                val permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (permissionToRecord && permissionToStore) {
                    Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    fun stopRecording() {
        startbtn?.isEnabled = true
        playbtn?.isEnabled = true
        stopplay?.isEnabled = true
        mRecorder?.stop()
        mRecorder?.release()
        mRecorder = null
        Toast.makeText(applicationContext, "Recording Stopped", Toast.LENGTH_LONG).show()
    }

    fun startRecording() {
        startbtn!!.isEnabled = false
        playbtn?.isEnabled = false
        stopplay?.isEnabled = false
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder!!.setOutputFile(mFileName)
        try {
            mRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "prepare() failed")
        }
        mRecorder?.start()
        Toast.makeText(applicationContext, "Recording Started", Toast.LENGTH_LONG)
            .show()

    }

    private fun checkPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, RECORD_AUDIO)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE),
            REQUEST_AUDIO_PERMISSION_CODE
        )
    }
}