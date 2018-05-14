package ru.jufy.myposh.di

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.inject.Scope

/**
 * Created by rolea on 4/22/2017.
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
annotation class ActivityContext
