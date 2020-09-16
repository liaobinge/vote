package com.nokia.vote

import android.app.Application

class AppApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        SharedPrefUtil.getInstance().init(this,null)
    }
}