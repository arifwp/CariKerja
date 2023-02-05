package com.amikom.carikerja.ui.profile.profile_details.summary

import android.util.Log
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
class SummaryViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "SummaryViewModel"

    private val _updateSummary = MutableLiveData<BaseResponse<String>>()
    val updateSummary: LiveData<BaseResponse<String>> = _updateSummary

    fun update(uid: String, summary: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "update: $summary")
                val ref = database.reference.child("Users").child(uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("summary")){
                            val summaryUpdate = hashMapOf<String, Any>(
                                "summary" to summary
                            )
                            database.reference.child("Users").child(uid).updateChildren(summaryUpdate).addOnSuccessListener {
                                _updateSummary.postValue(BaseResponse.Success("Berhasil mengubah ringkasan diri"))
                            }.addOnFailureListener {
                                _updateSummary.postValue(BaseResponse.Error(it.message.toString()))
                            }
                        } else {
                            database.reference.child("Users").child(uid).child("summary").setValue(summary).addOnSuccessListener {
                                _updateSummary.postValue(BaseResponse.Success("Berhasil mengubah data"))
                            }.addOnFailureListener {
                                _updateSummary.postValue(BaseResponse.Error(it.message))
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _updateSummary.postValue(BaseResponse.Error(error.message))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _updateSummary.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}