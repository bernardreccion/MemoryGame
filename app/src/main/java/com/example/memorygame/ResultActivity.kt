package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityResultBinding
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var chronometer: Chronometer
    private var backPressedTime: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chronometer = binding.chronTimer
        val timeFinished = intent.getLongExtra(TIMEWHENSTOPPED,chronometer.base)
        val score = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeFinished)),
                TimeUnit.MILLISECONDS.toSeconds(timeFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeFinished)))

        binding.chronTimer.text = score

        binding.buttonNewGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.buttonQuit.setOnClickListener {
            finishAffinity()
            exitProcess(-1)
        }

    }

    //add toast back function
    override fun onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(applicationContext, "Tap again to move to home screen", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

}