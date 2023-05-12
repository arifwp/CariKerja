package com.amikom.carikerja.ui.notification

import android.util.Log
import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.Message
import com.amikom.carikerja.models.Notification
import com.amikom.carikerja.repository.NetworkRepository
import com.amikom.carikerja.utils.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase,
    private val repository: NetworkRepository
) : ViewModel(), LifecycleObserver {

    private val TAG = "NotificationViewModel"

    private val _postNotificationResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val postNotificationResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _postNotificationResponse

    fun postNotification(registration_ids: Array<String>, title: String, body: String){
        viewModelScope.launch {
            try {

                val notif = Notification(
                    title = title,
                    body = body
                )

                val message = Message (
                    registration_ids = registration_ids,
                    data = notif
                )

                val response = repository.postNotification(message = message)
                Log.d(TAG, "message: $message")
                if (response.code() == 200){
                    Log.d(TAG, "response_code: ${response.code()}")
                    _postNotificationResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil mengirim pemberitahuan")))
                } else {
                    Log.d(TAG, "response_message: ${response.message()}")
                    Log.d(TAG, "errorBody: ${response.errorBody()}")
                    _postNotificationResponse.postValue(SingleLiveEvent(BaseResponse.Error("Gagal " + response.message().toString())))
                }

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _postNotificationResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }


}