package com.SigmaDating.app.advertising.viewmodel

import androidx.lifecycle.ViewModel
import com.SigmaDating.app.repository.MainRepository
import com.SigmaDating.app.storage.SharedPreferencesStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdvertisingActivityViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val sharedPreferencesStorage: SharedPreferencesStorage
) : ViewModel() {






}