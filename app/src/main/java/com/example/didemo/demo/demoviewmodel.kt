package com.example.didemo.demo

import androidx.lifecycle.ViewModel
import com.example.didemo.CountIncrementor
import javax.inject.Inject

class demoviewmodel  : ViewModel() {

    @Inject
    lateinit var countIncrementor: CountIncrementor

}