package com.example.didemo.sampleProjectAdvancedDi

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {


    //@Binds only links interface → implementation. interface is AnalyticsTrackerInterface and implementation is TrackingAnalytics
    /*
    *
    *
@Binds does not create the object. It only registers a rule in the graph:
When someone asks for AnalyticsTrackerInterface
Then give them a TrackingAnalytics instance.
So:
Return type = “what is requested” (the interface).
Parameter type = “what to provide” (the implementation).
Hilt still needs to know how to create TrackingAnalytics. That comes from TrackingAnalytics’s @Inject constructor(...). So the full flow is:
Some class (e.g. ViewModel) has @Inject constructor(analytics: AnalyticsTrackerInterface).
Hilt looks for a binding for AnalyticsTrackerInterface.
It finds your @Binds method: “for AnalyticsTrackerInterface, use TrackingAnalytics.”
It creates (or reuses) a TrackingAnalytics using its @Inject constructor.
It passes that instance wherever AnalyticsTrackerInterface was requested.
So @Binds = “when they ask for the interface, give them this implementation.” No method body, no return; the “link” is expressed by the method signature.

    *
    * */
    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(impl: TrackingAnalytics): AnalyticsTrackerInterface
}