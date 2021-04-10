package com.example.memorygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.Toast
import com.example.memorygame.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var cards: List<Memory>
    private lateinit var images: MutableList<Int>
    private lateinit var chronometer: Chronometer
    private var timeWhenStopped: Long = 0
    private var backPressedTime: Long = 0
    private var indexOfSingleCard: Int? = null //init, no cards are selected on the board
    private val btnImages by lazy {
        listOf(binding.imageButton1, binding.imageButton2, binding.imageButton3, binding.imageButton4, binding.imageButton5,
                binding.imageButton6, binding.imageButton7, binding.imageButton8, binding.imageButton9, binding.imageButton10,
                binding.imageButton11, binding.imageButton12, binding.imageButton13, binding.imageButton14, binding.imageButton15,
                binding.imageButton16, binding.imageButton17, binding.imageButton18, binding.imageButton19, binding.imageButton20)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chronometer = binding.chronTimer

        if (savedInstanceState?.getIntArray(IMAGES) != null) {
            images = savedInstanceState.getIntArray(IMAGES)?.toMutableList()!!
        } else {
            initializeImages()
        }

        if (savedInstanceState?.getParcelableArray(CARDS) != null) {
            cards = savedInstanceState.getParcelableArray(CARDS)?.toList() as List<Memory>
            updateView()
        } else {
            cards = btnImages.indices.map { index -> Memory(images[index]) }
        }

        if (savedInstanceState?.getLong(TIMER) != null) {
            chronometer.base = savedInstanceState.getLong(TIMER)
            timeWhenStopped = savedInstanceState.getLong(TIMEWHENSTOPPED)
        }

        //start timer
        chronometer.start()

        //iterate through the list of buttons and set the image resource for that index
        btnImages.forEachIndexed { index, button ->
            //set all image buttons to backcard
            if(savedInstanceState==null) {
                button.setImageResource(R.drawable.backcard)
            }
            //when user taps on a button, that's changing the state of the underlying memory
            button.setOnClickListener {
                //update the model
                updateModel(index)
                //update the view
                updateView()
            }
        }

        //exit process this activity once the user moves to the next activity
        if (intent.getBooleanExtra(EXIT, false)) {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putIntArray(IMAGES, images.toIntArray())
        outState.putParcelableArray(CARDS, cards.toTypedArray())
        outState.putLong(TIMER, chronometer.base)
        outState.putLong(TIMEWHENSTOPPED, timeWhenStopped)
        super.onSaveInstanceState(outState)
    }

    //pauses the timer when the app closes
    override fun onPause() {
        timeWhenStopped = chronometer.base - SystemClock.elapsedRealtime()
        chronometer.stop()
        super.onPause()
    }

    //resumes the timer when the app opens
    override fun onResume() {
        chronometer.base = SystemClock.elapsedRealtime() + timeWhenStopped
        chronometer.start()
        super.onResume()
    }

    private fun initializeImages() {
        images = mutableListOf(R.drawable.frontcard0, R.drawable.frontcard1, R.drawable.frontcard2, R.drawable.frontcard3, R.drawable.frontcard4,
                R.drawable.frontcard5, R.drawable.frontcard6, R.drawable.frontcard7, R.drawable.frontcard8, R.drawable.frontcard9)
        //add images twice
        images.addAll(images)
        //randomize images
        images.shuffle()
    }

    private fun updateView() {
        for(i in cards.indices) {
            //for every card in the game, get the corresponding button by using index
            val card = cards[i]
            if(card.isFaceUp) {
                //the underlying image will be the identifier of that card
                btnImages[i].setImageResource(card.identifier)
            } else {
                //return to default value which is backcard
                btnImages[i].setImageResource(R.drawable.backcard)
            }
        }

        //check if all cards are faced up
        if(countMatched() == cards.size) {
            //move to next activity
            val intent = Intent(this, ResultActivity::class.java)
            //set the time when all cards are faced up and pass it on the next activity
            timeWhenStopped = SystemClock.elapsedRealtime() - chronometer.base
            chronometer.stop()
            intent.putExtra(TIMEWHENSTOPPED, timeWhenStopped)

            //this intent allows to clear/finish this activity when the user moves to the next activity
            val intent2 = Intent(this, GameActivity::class.java)
            intent2.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent2.putExtra(EXIT, true)
            startActivity(intent2)
            startActivity(intent)
        }
    }

    private fun updateModel(position: Int) {
        //grab the card on that index
        val card = cards[position]
        //error check
        if(card.isFaceUp) {
            Toast.makeText(this, "Card is already face up", Toast.LENGTH_SHORT).show()
            return
        }

        //0 or 2 selected cards previously
        if(indexOfSingleCard == null) {
            //at the end of this turn, there is now a single selected card
            restoreCards()
            indexOfSingleCard = position
        } else {
            //exactly 1 card was selected previously
            checkMatch(indexOfSingleCard!!, position)
            //there is no single selected card at that point
            indexOfSingleCard = null
        }
        card.isFaceUp = !card.isFaceUp
    }

    private fun restoreCards() {
        for(card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    private fun checkMatch(position1 :Int, position2 :Int) {
        if(cards[position1].identifier == cards[position2].identifier) {
            cards[position1].isMatched = true
            cards[position2].isMatched = true
        }
    }

    //count matched cards
    private fun countMatched() : Int{
        var ctr = 0
        for(card in cards) {
            if (card.isMatched) {
                ctr += 1
            }
        }
        return ctr
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