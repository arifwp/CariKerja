package com.amikom.carikerja.ui.profile.profile_details.work_experience

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.Exp
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
class WorkExperienceViewModel@Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "WorkExperienceViewModel"

    private val _addWorkExperienceResponse = MutableLiveData<BaseResponse<String>>()
    val addWorkExperienceResponse: LiveData<BaseResponse<String>> = _addWorkExperienceResponse

    private val _getWorkExpResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<Exp>>>>()
    val getWorkExp: LiveData<SingleLiveEvent<BaseResponse<MutableList<Exp>>>> = _getWorkExpResponse

    fun getWorkExp(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("work_experience")
                val workExpList: MutableList<Exp> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        workExpList.clear()
                        for (childSnapshot in snapshot.children){
                            val workExperience: Exp? = childSnapshot.getValue(Exp::class.java)
                            if (workExperience != null) {
                                workExpList.add(workExperience)
                            }
                            _getWorkExpResponse.postValue(SingleLiveEvent(BaseResponse.Success(workExpList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getWorkExpResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getWorkExpResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun addWorkExperience(uid: String, exp: Exp){
        viewModelScope.launch {
            try {
                database.reference.child("Users").child(uid).child("work_experience").push().setValue(exp)
                    .addOnSuccessListener {
                        _addWorkExperienceResponse.postValue(BaseResponse.Success("Berhasil menambahkan data"))
                    }.addOnFailureListener {
                        _addWorkExperienceResponse.postValue(BaseResponse.Error(it.message.toString()))
                    }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addWorkExperienceResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}