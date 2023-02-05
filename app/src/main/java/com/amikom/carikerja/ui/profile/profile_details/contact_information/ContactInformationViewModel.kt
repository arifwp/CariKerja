package com.amikom.carikerja.ui.profile.profile_details.contact_information

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
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
class ContactInformationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "ContactInformationViewModel"

    private val _updateResponse = MutableLiveData<BaseResponse<String>>()
    val updateResponse: LiveData<BaseResponse<String>> = _updateResponse

    fun update(uid: String, phone: String, email: String, address: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("address")){
                            val dataUpdate = hashMapOf<String, Any>(
                                "phone" to phone,
                                "email" to email,
                                "address" to address
                            )
                            database.reference.child("Users").child(uid).updateChildren(dataUpdate).addOnSuccessListener {
                                _updateResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                            }.addOnFailureListener {
                                _updateResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }
                        } else {
                            val data = hashMapOf<String, Any>(
                                "phone" to phone,
                                "email" to email
                            )
                            database.reference.child("Users").child(uid).child("address").setValue(address).addOnSuccessListener {
                                _updateResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                            }.addOnFailureListener {
                                _updateResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }
                            database.reference.child("Users").child(uid).updateChildren(data).addOnSuccessListener {
                                _updateResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                            }.addOnFailureListener {
                                _updateResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _updateResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _updateResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}