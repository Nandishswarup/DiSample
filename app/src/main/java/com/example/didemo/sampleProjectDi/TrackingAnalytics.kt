package com.example.didemo.sampleProjectDi

import javax.inject.Inject

class TrackingAnalytics @Inject constructor(val printLogger:PrintLogger) {

    fun trackEvent(event: String) {
        printLogger.logMessage(event)
    }

}