package ru.jufy.myposh

import android.app.Activity
import android.app.Application
import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import ru.jufy.myposh.di.component.DaggerApplicationComponent

import ru.jufy.myposh.entity.KulonToken
import javax.inject.Inject

/**
 * Created by BorisDev on 26.07.2017.
 */

class MyPoshApplication : Application() , HasActivityInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        app = this
        initDagger()
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector

    }

    private fun initDagger() {
        DaggerApplicationComponent
                .builder()
                .create(this).inject(this)
    }

    companion object {

        private val DEBUG_URL = "https://posh.jwma.ru/api/v1/"
        val DOMAIN = DEBUG_URL

        var currentToken: KulonToken? = null
            private set

        private var app: MyPoshApplication? = null

        val context: Context
            get() = app!!.applicationContext

        fun onNewTokenObtained(newToken: KulonToken) {
            currentToken = newToken
        }
    }

}
