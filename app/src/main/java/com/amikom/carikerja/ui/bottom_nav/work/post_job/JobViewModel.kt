package com.amikom.carikerja.ui.bottom_nav.work.post_job

import androidx.lifecycle.*
import com.amikom.carikerja.models.*
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
class JobViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "PostJobViewModel"

    private val _addJobResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val addJobResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _addJobResponse

    private val _getJobResponse = MutableLiveData<BaseResponse<MutableList<JobDetails>>>()
    val getJobResponse: LiveData<BaseResponse<MutableList<JobDetails>>> = _getJobResponse

    private val _addApplicantResponse = MutableLiveData<BaseResponse<String>>()
    val addAppplicantResponse: LiveData<BaseResponse<String>> = _addApplicantResponse

    private val _getUserIdJobResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<String>>>>()
    val getUserIdJobResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<String>>>> = _getUserIdJobResponse



    fun addApplicant(uid: String, applicant: Applicant?, historyJob: HistoryJob?){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs").child(applicant?.id_job.toString()).push()
                applicant?.id_applicant = ref.key


                val queryUser = database.reference.child("Users").child(uid.toString()).child("history_job").push()
                queryUser.setValue(historyJob)
                    .addOnSuccessListener {
                        ref.setValue(applicant)
                            .addOnSuccessListener {
                                _addApplicantResponse.postValue(BaseResponse.Success("Berhasil melamar"))
                            }.addOnFailureListener {
                                _addApplicantResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }
                    }.addOnFailureListener {
                        _addApplicantResponse.postValue(BaseResponse.Error(it.message.toString()))
                    }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addApplicantResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getUserIdJob(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid.toString()).child("history_job")
                val historyJobList: MutableList<String> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        historyJobList.clear()
                        for (childSnapshot in snapshot.children){
                            val historyJob: String? = childSnapshot.getValue(HistoryJob::class.java)?.id_job
                            if (historyJob != null){
                                historyJobList.add(historyJob)
                            }
                            _getUserIdJobResponse.postValue(SingleLiveEvent(BaseResponse.Success(historyJobList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getUserIdJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getUserIdJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun addJob(uid: String, jobDetails: JobDetails, publishedJob: PublishedJob?){
        viewModelScope.launch {
            try {
                val queryUser = database.reference.child("Users").child(uid).child("published_jobs").push()

                val query = database.reference.child("Users").child(uid)
                val ref = database.reference.child("Jobs").push()
                publishedJob?.id_job = ref.key
                queryUser.setValue(publishedJob).addOnFailureListener {
                    _addJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                }

                query.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val name = snapshot.child("name").getValue(String::class.java).toString()
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java).toString()

                        jobDetails.id = ref.key
                        publishedJob?.id_job = ref.key
                        jobDetails.uid = uid
                        jobDetails.person_who_post = name
                        jobDetails.image_url = imageUrl
                        ref.setValue(jobDetails).addOnSuccessListener {
                            _addJobResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil posting pekerjaan")))
                        }.addOnFailureListener {
                            _addJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _addJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }
                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
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