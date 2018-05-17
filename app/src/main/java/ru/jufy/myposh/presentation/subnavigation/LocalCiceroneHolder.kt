package com.jufy.mgtshr.ui.subnavigation

import java.util.HashMap

import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

/**
 * Created by rolea on 18.09.2017.
 */

class LocalCiceroneHolder {
    private var containers:HashMap<String, Cicerone<Router>> = HashMap()

    fun getCicerone(containerTag: String): Cicerone<Router>? {
        if (!containers.containsKey(containerTag)) containers.put(containerTag, Cicerone.create())
        /*(!containers.containsKey(containerTag)).let {
            containers.put(containerTag, Cicerone.create())
        }*/

        return containers[containerTag]
    }
}