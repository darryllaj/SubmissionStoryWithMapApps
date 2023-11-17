package com.dicoding.submissionstoryapps.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.submissionstoryapps.Response.ErrorResponse
import com.dicoding.submissionstoryapps.Response.ListStoryItem
import com.dicoding.submissionstoryapps.repository.Repository
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class MapsViewModels(private val repository: Repository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> get() = _isLoading
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> get() = _listStory

    private val _errMsg = MutableLiveData<String>()
    val errMsg : LiveData<String> get() = errMsg

    suspend fun getAllMarkStory(){
        val token = repository.getSession().first().token
        val markStory = repository.getMarkStory("Bearer $token")
        val message = markStory.message
        try {
            _isLoading.value = false
            _listStory.value = markStory.listStory
            _errMsg.value = message!!
        } catch (e: HttpException){
            _isLoading.value = false
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            errorMessage!!
        }
    }
}