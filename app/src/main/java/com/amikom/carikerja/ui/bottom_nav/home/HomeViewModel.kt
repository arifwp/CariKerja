package com.amikom.carikerja.ui.bottom_nav.home

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.repository.NetworkRepository
import com.amikom.carikerja.response.InvoiceStatusResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NetworkRepository
) : ViewModel(), LifecycleObserver {

    private val TAG = "HomeViewModel"

    private val _getInvoiceStatusResponse = MutableLiveData<BaseResponse<InvoiceStatusResponse?>>()
    val getInvoiceStatusResponse: LiveData<BaseResponse<InvoiceStatusResponse?>> = _getInvoiceStatusResponse

    fun getInvoiceStatus(partner_tx_id: String){
        viewModelScope.launch {
            try {
//                val response = repository.getInvoiceStatus(partner_tx_id)
//                if (response.code() == 200){
//                    _getInvoiceStatusResponse.postValue(BaseResponse.Success(response.body()))
//                } else {
//                    _getInvoiceStatusResponse.postValue(BaseResponse.Error(response.message()))
//                }
            } catch (e: Exception) {
                _getInvoiceStatusResponse.postValue(BaseResponse.Error(e.message.toString()))
            }
        }
    }

}