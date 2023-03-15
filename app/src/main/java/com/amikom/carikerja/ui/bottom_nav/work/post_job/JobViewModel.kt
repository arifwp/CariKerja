package com.amikom.carikerja.ui.bottom_nav.work.post_job

import androidx.lifecycle.*
import com.amikom.carikerja.models.Applicant
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
class JobViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "PostJobViewModel"

    private val _addJobResponse = MutableLiveData<BaseResponse<String>>()
    val addJobResponse: LiveData<BaseResponse<String>> = _addJobResponse

    private val _getJobResponse = MutableLiveData<BaseResponse<MutableList<JobDetails>>>()
    val getJobResponse: LiveData<BaseResponse<MutableList<JobDetails>>> = _getJobResponse

    private val _addApplicantResponse = MutableLiveData<BaseResponse<String>>()
    val addAppplicantResponse: LiveData<BaseResponse<String>> = _addApplicantResponse

    fun addApplicant(uid: String, applicant: Applicant){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs")

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addApplicantResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun addJob(uid: String, jobDetails: JobDetails){
        viewModelScope.launch {
            try {
                val query = database.reference.child("Users").child(uid)
                query.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.child("name").getValue(String::class.java).toString()
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java).toString()
                        val ref = database.reference.child("Jobs").push()
                        jobDetails.id = ref.key
                        jobDetails.uid = uid
                        jobDetails.person_who_post = name
                        jobDetails.image_url = imageUrl
                        ref.setValue(jobDetails).addOnSuccessListener {
                            _addJobResponse.postValue(BaseResponse.Success("Berhasil posting pekerjaan"))
                        }.addOnFailureListener {
                            _addJobResponse.postValue(BaseResponse.Error(it.message.toString()))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _addJobResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addJobResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getJob(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs")
                val dataJobList: MutableList<JobDetails> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataJobList.clear()
                        for (childSnapshot in snapshot.children){
                            val jobDetails: JobDetails? = childSnapshot.getValue(JobDetails::class.java)
                            if (jobDetails != null){
                                dataJobList.add(jobDetails)
                            }
                            _getJobResponse.postValue(BaseResponse.Success(dataJobList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getJobResponse.postValue(BaseResponse.Error(error.message))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getJobResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}