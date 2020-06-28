package com.example.demo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.demo.databinding.MainActivityLayoutBinding
import kotlinx.coroutines.Runnable

class MainActivity : AppCompatActivity() {

    private var binding: MainActivityLayoutBinding ?= null
    private var viewModel: MainViewModel?= null
    private var mService: DemoService? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel?.getBinderLiveDataObserver()?.observe(this, Observer {
            mService = it?.getDemoServiceInstance()
        })

        binding?.btnStatus?.setOnClickListener{
            mService?.let {
                if(it.progress >= it.maxValue){
                    it.resetTask()
                    binding?.btnStatus?.text = "Start"
                } else {
                    if(it.isPaused){
                        it.continueTask()
                        viewModel?.updateProgressValue(true)
                    } else {
                        it.pauseTask()
                        viewModel?.updateProgressValue(false)
                    }

                }
            }
        }

        viewModel?.getProgressUpdatingLiveDataObserver()?.observe(this, Observer {
            val handler = Handler()
            val runnable = object: Runnable {
                override fun run() {
                    if(viewModel?.getProgressUpdatingLiveDataObserver()?.value == true){
                        viewModel?.getBinderLiveDataObserver()?.value?.let {
                            if(mService?.progress == mService?.maxValue){
                                viewModel?.updateProgressValue(false)
                            }
                            binding?.progressBar?.progress = mService?.progress ?: 0
                            binding?.progressBar?.max = mService?.maxValue?: 5000

                            binding?.progressPercTv?.text = "${(100 * (mService?.progress ?: 0) / (mService?.maxValue?:5000))} %"
                        }
                        handler.postDelayed(this, 100)
                    } else {
                        handler.removeCallbacks(this)
                    }
                }
            }
            handler.postDelayed(runnable, 100)
            if(it){
                binding?.btnStatus?.text = "Pause"
                handler.postDelayed(runnable, 100)
            } else {
                if(mService?.progress == mService?.maxValue)binding?.btnStatus?.text = "Restart"
                else binding?.btnStatus?.text = "Start"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        startService()
    }

    private fun startService(){
        val intent = Intent(this, DemoService::class.java)
        startService(intent)
        bindService()
    }

    private fun bindService(){
        val intent = Intent(this, DemoService::class.java)
        this.bindService(intent, viewModel?.getServiceConnection()!!, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        viewModel?.getBinderLiveDataObserver()?.value?.let {
            unbindService(viewModel?.getServiceConnection()!!)
        }
    }

}
