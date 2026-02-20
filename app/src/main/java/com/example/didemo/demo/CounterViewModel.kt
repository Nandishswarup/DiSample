package com.example.didemo.demo

import androidx.lifecycle.ViewModel
import com.example.didemo.CountIncrementor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CounterViewModel @Inject constructor() : ViewModel()
{
    // CounterViewModel viewmodel = CounterViewModel()
    @Inject  // ← Field injection
    lateinit var countIncrementor: CountIncrementor
    //var CountIncrementor countIncrementor = CountIncrementor(abc, xyz)

    var count = 0
        private set


    fun buttonClicked(counter:Int)
    {
        count++
        countIncrementor.track("Counter updated:$count")
    }


}