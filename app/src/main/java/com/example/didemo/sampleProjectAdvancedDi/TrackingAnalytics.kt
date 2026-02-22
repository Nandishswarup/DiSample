package com.example.didemo.sampleProjectAdvancedDi

import javax.inject.Inject

class TrackingAnalytics @Inject constructor(val printLogger:PrintLogger):AnalyticsTrackerInterface
{
    override fun trackEvent(event: String) {
        printLogger.logMessage(event)
    }


}