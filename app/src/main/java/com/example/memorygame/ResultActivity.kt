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
    private var timeFinished: Long = 0
    private val db = DatabaseHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chronometer = binding.chronTimer
        timeFinished = intent.getLongExtra(TIMEWHENSTOPPED,chronometer.base)
        chronometer.stop()



        val score = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeFinished)),
                TimeUnit.MILLISECONDS.toSeconds(timeFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeFinished)))

        binding.chronTimer.text = score

        insertRecord()
        insertTop10()

        binding.buttonNewGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }

        binding.buttonLeaderboard.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

        binding.buttonQuit?.setOnClickListener {
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

    fun insertRecord() {
        var record = Record(timeFinished)
        var db = DatabaseHandler(this)
        db.insertData(record)
    }

    fun insertTop10() {
        var data = db.viewData()
        var top10 = data.take(10)

        for(i in 0..(top10.size-1)) {
            if(timeFinished <= top10[i].time) {
                Toast.makeText(this, "You made it to top 10!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}