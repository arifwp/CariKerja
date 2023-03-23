package com.amikom.carikerja.ui.bottom_nav.history_work

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.JobDetails
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
class HistoryJobViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val _getJobByUserResponse = MutableLiveData<BaseResponse<MutableList<JobDetails>>>()
    val getJobByUserResponse: LiveData<BaseResponse<MutableList<JobDetails>>> = _getJobByUserResponse

    fun getJobByUser(uidRecruiter: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs").orderByChild("uid").equalTo(uidRecruiter)
                val dataJobByUserList: MutableList<JobDetails> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataJobByUserList.clear()
                        for (childSnapshot in snapshot.children){
                            val dataJob: JobDetails? = childSnapshot.getValue(JobDetails::class.java)
                            if(dataJob != null){
                                dataJobByUserList.add(dataJob)
                            }
                            _getJobByUserResponse.postValue(BaseResponse.Success(dataJobByUserList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getJobByUserResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }
                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getJobByUserResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}