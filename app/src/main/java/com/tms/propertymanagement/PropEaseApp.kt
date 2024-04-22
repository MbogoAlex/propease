package com.propertymanagement.tms

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.propertymanagement.tms.container.AppContainer
import com.propertymanagement.tms.container.PropEaseMainContainer
import com.propertymanagement.tms.propEaseDataStore.DSRepository
import com.tms.propertymanagement.db.DBRepository

private const val DS_NAME = "PROP_EASE_DS"
private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = DS_NAME
)
class PropEaseApp: Application() {
    lateinit var container: AppContainer
    lateinit var dsRepository: DSRepository
    override fun onCreate() {
        super.onCreate()
        dsRepository = DSRepository(datastore)
        container = PropEaseMainContainer(this)
    }
}