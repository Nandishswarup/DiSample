package com.example.didemo.sampleProjectAdvancedDi

import javax.inject.Inject

class PrintLogger @Inject constructor()  {

     fun logMessage(message: String) {
        println("PRINT LOG: $message")
    }
}