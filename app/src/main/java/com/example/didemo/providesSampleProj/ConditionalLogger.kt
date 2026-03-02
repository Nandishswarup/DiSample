package com.example.didemo.providesSampleProj

class ConditionalLogger(val enabled:Boolean): InterfaceLogger {

    override fun logMessage(msg: String) {
        if(enabled)
                println(msg)
    }

}