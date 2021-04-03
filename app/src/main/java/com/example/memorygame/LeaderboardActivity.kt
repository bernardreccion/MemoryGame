package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memorygame.databinding.ActivityLeaderboardBinding
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLeaderboardBinding
    private val db = DatabaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewData()

        binding.buttonHome?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonQuit?.setOnClickListener {
            finishAffinity()
            exitProcess(-1)
        }

    }

    private fun viewData() {
        var data = db.viewData()

        if(data.size <= 0){
            binding.textViewLeaderboard.text = getString(R.string.leaderboard)
        }

        for (i in 0..(data.size-1)) {
            binding.textViewLeaderboard.append( (i+1).toString() + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" +
                    String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(data[i].time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(data[i].time)),
                        TimeUnit.MILLISECONDS.toSeconds(data[i].time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(data[i].time))) + "\n")
        }
    }
}