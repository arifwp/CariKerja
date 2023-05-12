package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.bank_account

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankAccountViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val _editBankDetailResponse = MutableLiveData<BaseResponse<String>>()
    val editBankDetailResponse: LiveData<BaseResponse<String>> = _editBankDetailResponse

    fun editBankDetail(uid: String?, bank_name: String, bank_account: String, bank_account_name: String){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Users").child(uid.toString())
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        database.reference.child("Users").child(uid.toString()).child("bank_name").setValue(bank_name)
                            .addOnSuccessListener {
                                _editBankDetailResponse.postValue(BaseResponse.Success("1"))
                            }
                            .addOnFailureListener {
                                _editBankDetailResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }

                        database.reference.child("Users").child(uid.toString()).child("bank_account").setValue(bank_account)
                            .addOnSuccessListener {
                                _editBankDetailResponse.postValue(BaseResponse.Success("2"))
                            }
                            .addOnFailureListener {
                                _editBankDetailResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }

                        database.reference.child("Users").child(uid.toString()).child("bank_account_name").setValue(bank_account_name)
                            .addOnSuccessListener {
                                _editBankDetailResponse.postValue(BaseResponse.Success("3"))
                            }
                            .addOnFailureListener {
                                _editBankDetailResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        _editBankDetailResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editBankDetailResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}