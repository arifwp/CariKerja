package com.amikom.carikerja.ui.bottom_nav.history_work

import android.util.Log
import androidx.lifecycle.*
import com.amikom.carikerja.models.Applicant
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.Job
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

    private val TAG = "HistoryJobViewModel"

    private val _getJobByUserResponse = MutableLiveData<BaseResponse<MutableList<JobDetails>>>()
    val getJobByUserResponse: LiveData<BaseResponse<MutableList<JobDetails>>> = _getJobByUserResponse

    private val _getApplicantResponse = MutableLiveData<BaseResponse<MutableList<Applicant>>>()
    val getApplicantResponse: LiveData<BaseResponse<MutableList<Applicant>>> = _getApplicantResponse

    private val _getListIdApplicantByPublishedJobsResponse = MutableLiveData<BaseResponse<ArrayList<String>>>()
    val getListIdApplicantByPublishedJobResponse: LiveData<BaseResponse<ArrayList<String>>> = _getListIdApplicantByPublishedJobsResponse

    fun getListIdApplicantByPublishedJob(uidRecruiter: String, idJob: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs").child(idJob).child("applicant")
                val dataJobByUserList: ArrayList<String> = arrayListOf(String())
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataJobByUserList.clear()
                        for (childSnapshot in snapshot.children){
                            val dataJob: String? = childSnapshot.getValue(Applicant::class.java)?.id_applicant
                            if(dataJob != null){
                                dataJobByUserList.add(dataJob)
                            }
                            _getListIdApplicantByPublishedJobsResponse.postValue(BaseResponse.Success(dataJobByUserList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getListIdApplicantByPublishedJobsResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }
                })


            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getListIdApplicantByPublishedJobsResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

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

    fun getApplicant(uid: String, id_job: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs").child(id_job).child("applicant")
                val applicantList: MutableList<Applicant> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        applicantList.clear()
                        for (childSnpashot in snapshot.children){
                            val applicant: Applicant? = childSnpashot.getValue(Applicant::class.java)
                            if (applicant != null){
                                applicantList.add(applicant)
                            }
                            _getApplicantResponse.postValue(BaseResponse.Success(applicantList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getApplicantResponse.postValue(BaseResponse.Error(error.message))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getApplicantResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}