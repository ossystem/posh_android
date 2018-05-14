package ru.jufy.myposh.di.module;

import android.app.Activity;
import android.content.Context;

import ru.jufy.myposh.di.PerActivity;

import dagger.Binds;
import dagger.Module;

/**
 * Created by rolea on 27.10.2017.
 */

@Module
public abstract class BaseActivityModule {
    @Binds
    @PerActivity
    abstract Context activityContext(Activity activity);
}
