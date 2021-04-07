package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.databinding.ActivityResultBinding
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var chronometer: Chronometer
    private var backPressedTime: Long = 0
    private var timeFinished: Long = 0
    private lateinit var score: String
    private val db = DatabaseHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chronometer = binding.chronTimer

        //get the intent from previous activity
        timeFinished = intent.getLongExtra(TIMEWHENSTOPPED,chronometer.base)
        chronometer.stop()


        //string format to display 00:00 for better readability (from Long to String format)
        score = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(timeFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeFinished)),
                TimeUnit.MILLISECONDS.toSeconds(timeFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeFinished)))

        binding.chronTimer.text = score

        isQualifiedTop10()
        insertRecord()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_result, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.itemShare) {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "LoL Champions: Memory Game: \n You have finished the game at $score on ${Calendar.getInstance().time}")

                type = "text/plain"
            }
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //insert record to the database
    private fun insertRecord() {
        val record = Record(timeFinished)
        val db = DatabaseHandler(this)
        db.insertData(record)
    }

    //check if the current time makes it to the top 10 of the leaderboard
    private fun isQualifiedTop10() {
        val data = db.viewData()
        val top10 = data.take(10)

        for(i in 0..(top10.size-1)) {
            if(timeFinished <= top10[i].time) {
                Toast.makeText(this, "You made it to top 10!", Toast.LENGTH_SHORT).show()
                break
            }
        }
    }



}