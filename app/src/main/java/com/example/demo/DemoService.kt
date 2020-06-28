package com.example.demo

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder

class DemoService: Service() {

    private val TAG = "DemoService.class"

    private val binder = DemoBinder()
    private var mHandler: Handler? = null

    var progress: Int=0
        get() = field
        set(value){field = value}

    var maxValue = 5000
        get() = field
        set(value){field = value}

    var isPaused: Boolean = true
        get() = field
        set(value){field = value}

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class DemoBinder: Binder(){
        fun getDemoServiceInstance(): DemoService {
            return this@DemoService
        }
    }

    fun pauseTask(){
        isPaused = true
    }

    fun continueTask(){
        isPaused = false
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if(progress >= maxValue || isPaused){
                    mHandler!!.removeCallbacks(this)
                    pauseTask()
                } else {
                    progress += 100
                    mHandler?.postDelayed(this, 100)
                }
            }
        }
        mHandler?.postDelayed(runnable, 1000)
    }

    fun resetTask(){
        progress = 0
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}