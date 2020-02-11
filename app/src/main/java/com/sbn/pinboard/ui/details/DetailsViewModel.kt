package com.sbn.pinboard.ui.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sbn.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DetailsViewModel @Inject constructor() : ViewModel() {
    val user = MutableLiveData<User>()
}
