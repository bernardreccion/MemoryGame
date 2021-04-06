package com.example.memorygame

// Record class to store records for database
class Record {
    var id: Int = 0
    var time: Long = 0

    constructor(time: Long) {
        this.time = time
    }
    constructor()
}