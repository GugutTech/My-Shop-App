package net.gugut.mypayapp.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object UserPreferences {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    private val EMAIL_KEY = stringPreferencesKey("remembered_email")

    suspend fun saveEmail(context: Context, email: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
        }
    }

    suspend fun clearEmail(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(EMAIL_KEY)
        }
    }

    suspend fun getSavedEmail(context: Context): String? {
        val prefs = context.dataStore.data.first()
        return prefs[EMAIL_KEY]
    }
}

