package com.example.didemo.sampleProjectDi

import javax.inject.Inject

class PrintLogger @Inject constructor()  {

     fun logMessage(message: String) {
        println("PRINT LOG: $message")
    }
}