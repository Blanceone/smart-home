package com.smarthome.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    private object Keys {
        val DEVICE_ID = stringPreferencesKey("device_id")
        val USER_ID = stringPreferencesKey("user_id")
        val NICKNAME = stringPreferencesKey("nickname")
        val AVATAR = stringPreferencesKey("avatar")
        
        val CACHED_SCHEMES = stringPreferencesKey("cached_schemes")
        val CACHED_HOUSE_LAYOUT = stringPreferencesKey("cached_house_layout")
        val CACHED_USER_INFO = stringPreferencesKey("cached_user_info")
        
        val CACHE_TIMESTAMP_SCHEMES = longPreferencesKey("cache_timestamp_schemes")
        val CACHE_TIMESTAMP_HOUSE_LAYOUT = longPreferencesKey("cache_timestamp_house_layout")
        val CACHE_TIMESTAMP_USER_INFO = longPreferencesKey("cache_timestamp_user_info")
    }

    private var cachedDeviceId: String? = null

    val deviceId: Flow<String?> = context.dataStore.data
        .map { prefs ->
            cachedDeviceId = prefs[Keys.DEVICE_ID]
            cachedDeviceId
        }

    fun getDeviceIdSync(): String? = cachedDeviceId

    fun setCachedDeviceId(deviceId: String?) {
        cachedDeviceId = deviceId
    }

    val userId: Flow<String?> = context.dataStore.data
        .map { it[Keys.USER_ID] }

    val nickname: Flow<String?> = context.dataStore.data
        .map { it[Keys.NICKNAME] }

    val avatar: Flow<String?> = context.dataStore.data
        .map { it[Keys.AVATAR] }

    val isRegistered: Flow<Boolean> = userId.map { !it.isNullOrEmpty() }

    suspend fun saveUser(
        deviceId: String,
        userId: String,
        nickname: String? = null,
        avatar: String? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEVICE_ID] = deviceId
            prefs[Keys.USER_ID] = userId
            nickname?.let { prefs[Keys.NICKNAME] = it }
            avatar?.let { prefs[Keys.AVATAR] = it }
        }
    }

    suspend fun updateUser(nickname: String?, avatar: String?) {
        context.dataStore.edit { prefs ->
            nickname?.let { prefs[Keys.NICKNAME] = it }
            avatar?.let { prefs[Keys.AVATAR] = it }
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.DEVICE_ID)
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.NICKNAME)
            prefs.remove(Keys.AVATAR)
            prefs.remove(Keys.CACHED_SCHEMES)
            prefs.remove(Keys.CACHED_HOUSE_LAYOUT)
            prefs.remove(Keys.CACHED_USER_INFO)
            prefs.remove(Keys.CACHE_TIMESTAMP_SCHEMES)
            prefs.remove(Keys.CACHE_TIMESTAMP_HOUSE_LAYOUT)
            prefs.remove(Keys.CACHE_TIMESTAMP_USER_INFO)
        }
    }

    suspend fun cacheSchemes(schemesJson: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CACHED_SCHEMES] = schemesJson
            prefs[Keys.CACHE_TIMESTAMP_SCHEMES] = System.currentTimeMillis()
        }
    }

    fun getCachedSchemes(): Flow<Pair<String?, Long>> = context.dataStore.data.map { prefs ->
        val data = prefs[Keys.CACHED_SCHEMES]
        val timestamp = prefs[Keys.CACHE_TIMESTAMP_SCHEMES] ?: 0L
        Pair(data, timestamp)
    }

    suspend fun cacheHouseLayout(houseLayoutJson: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CACHED_HOUSE_LAYOUT] = houseLayoutJson
            prefs[Keys.CACHE_TIMESTAMP_HOUSE_LAYOUT] = System.currentTimeMillis()
        }
    }

    fun getCachedHouseLayout(): Flow<Pair<String?, Long>> = context.dataStore.data.map { prefs ->
        val data = prefs[Keys.CACHED_HOUSE_LAYOUT]
        val timestamp = prefs[Keys.CACHE_TIMESTAMP_HOUSE_LAYOUT] ?: 0L
        Pair(data, timestamp)
    }

    suspend fun cacheUserInfo(userInfoJson: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CACHED_USER_INFO] = userInfoJson
            prefs[Keys.CACHE_TIMESTAMP_USER_INFO] = System.currentTimeMillis()
        }
    }

    fun getCachedUserInfo(): Flow<Pair<String?, Long>> = context.dataStore.data.map { prefs ->
        val data = prefs[Keys.CACHED_USER_INFO]
        val timestamp = prefs[Keys.CACHE_TIMESTAMP_USER_INFO] ?: 0L
        Pair(data, timestamp)
    }

    fun isCacheValid(timestamp: Long, maxAgeMillis: Long = 24 * 60 * 60 * 1000): Boolean {
        return System.currentTimeMillis() - timestamp < maxAgeMillis
    }
}
