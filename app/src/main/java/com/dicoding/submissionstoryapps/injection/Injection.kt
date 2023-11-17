package com.dicoding.submissionstoryapps.injection

import android.content.Context
import com.dicoding.submissionstoryapps.database.StoryDatabase
import com.dicoding.submissionstoryapps.pref.UserPreference
import com.dicoding.submissionstoryapps.pref.dataStore
import com.dicoding.submissionstoryapps.repository.Repository
import com.dicoding.submissionstoryapps.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val database = StoryDatabase.getInstance(context)
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(pref, apiService, database)
    }
}