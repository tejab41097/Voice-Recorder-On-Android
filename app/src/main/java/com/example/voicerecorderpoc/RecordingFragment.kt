package com.example.voicerecorderpoc

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton


class RecordingFragment : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recording, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val maiActivity = requireActivity() as MainActivity
        maiActivity.startRecording()
        Glide.with(requireContext())
            .load(R.drawable.gif_recorder)
            .into(view.findViewById(R.id.iv_gif))
        var counter = 0
        val temp = object : CountDownTimer(100000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                try {
                    view.findViewById<TextView>(R.id.tv_timer).text =
                        "${counter / 60}:${counter % 60}"
                    counter++
                } catch (ex: Exception) {
                }
            }

            override fun onFinish() {
                try {
                    maiActivity.stopRecording()
                    dismiss()
                } catch (ex: Exception) {
                }
            }
        }.start()

        view.findViewById<FloatingActionButton>(R.id.btn_delete).setOnClickListener {
            maiActivity.stopRecording()
            temp.cancel()
            dismiss()
        }
        view.findViewById<FloatingActionButton>(R.id.btn_send).setOnClickListener {
            maiActivity.stopRecording()
            temp.cancel()
            dismiss()
        }
    }
}