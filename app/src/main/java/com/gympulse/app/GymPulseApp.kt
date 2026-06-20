package com.gympulse.app

import android.app.Application
import com.gympulse.app.data.AppDatabase
import com.gympulse.app.data.DataManager
import com.gympulse.app.data.PreferenceManager
import com.gympulse.app.data.TrainingRepository

class GymPulseApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }

    val repository by lazy { TrainingRepository(database.trainingLogDao()) }

    val preferenceManager by lazy { PreferenceManager(this) }

    val dataManager by lazy { DataManager(this, repository) }
}
