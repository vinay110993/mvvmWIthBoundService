package com.example.demo

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private val isProgressUpdating = MutableLiveData<Boolean>()
    private val binder = MutableLiveData<DemoService.DemoBinder>()

    fun getServiceConnection() = sessionConnection
    fun getProgressUpdatingLiveDataObserver()= isProgressUpdating
    fun getBinderLiveDataObserver() = binder

    fun updateProgressValue(value: Boolean) = isProgressUpdating.postValue(value)

    private val sessionConnection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            binder.postValue(null)
        }
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binder.postValue(service as DemoService.DemoBinder?)
        }
    }
}