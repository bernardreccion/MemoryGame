package com.example.memorygame

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//isFaceUp - check if a card is faced up; set to false because initially all cards are face down
//isMatched - check if both cards are matched; set to false because initially all cards are face down so there are no matched cards
//identifier - when pairing cards, we compare them by looking at this identifier
@Parcelize
data class Memory(val identifier: Int, var isFaceUp:Boolean = false, var isMatched:Boolean = false) : Parcelable