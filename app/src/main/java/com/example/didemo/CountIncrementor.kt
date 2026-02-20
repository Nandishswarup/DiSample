package com.example.didemo

import javax.inject.Inject

class CountIncrementor @Inject constructor(){

    fun track(event: String) {
        println("Tracked Event: " + event)
    }
}