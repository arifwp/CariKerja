package com.amikom.carikerja.ui.bottom_nav.work.post_job

import android.util.Log
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

    private val TAG = "JobViewModel"

    private val _addJobResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val addJobResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _addJobResponse

    private val _editJobResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val editJobResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _editJobResponse

    private val _getJobResponse = MutableLiveData<BaseResponse<MutableList<JobDetails>>>()
    val getJobResponse: LiveData<BaseResponse<MutableList<JobDetails>>> = _getJobResponse

    private val _getDetailJobResponse = MutableLiveData<SingleLiveEvent<BaseResponse<JobDetails>>>()
    val getJobDetailResponse: LiveData<SingleLiveEvent<BaseResponse<JobDetails>>> = _getDetailJobResponse

    private val _addApplicantResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val addAppplicantResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _addApplicantResponse

    private val _getUserIdJobResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<String>>>>()
    val getUserIdJobResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<String>>>> = _getUserIdJobResponse

    private val _getPublishedJobByRecruiterUidResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<JobDetails>>>>()
    val getPublishedJobByRecruiterUidResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<JobDetails>>>> = _getPublishedJobByRecruiterUidResponse

    private val _getTotalApplicantResponse = MutableLiveData<BaseResponse<Int>>()
    val getTotalApplicantResponse: LiveData<BaseResponse<Int>> = _getTotalApplicantResponse

    private val _getIdApplicantResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<String>>>>()
    val getIdApplicantResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<String>>>> = _getIdApplicantResponse

    private val _chooseApplicantResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val chooseApplicantResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _chooseApplicantResponse

    private val _getJobStatusResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val getJobStatusResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _getJobStatusResponse

    private val _deleteJobResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val deleteJobResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _deleteJobResponse

    fun deleteJob(uidRecruiter: String, idJob: String){
        viewModelScope.launch {
            try {

                val queryJobs = database.reference.child("Jobs").child(idJob)

                queryJobs.removeValue().addOnSuccessListener {


                    val queryPublishedJob = database.reference.child("Users").child(uidRecruiter).child("published_jobs").orderByChild("id_job").equalTo(idJob)
                    queryPublishedJob.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                for (childSnapshot in snapshot.children){

                                    val key = childSnapshot.key
                                    Log.d(TAG, "queryPublishedJob_key: $key")

                                    val usersPublishedJobs = database.reference.child("Users").child(uidRecruiter).child(idJob).child("published_jobs").child(key.toString())

                                    Log.d(TAG, "usersPublishedJobs_key: ${usersPublishedJobs.key}")
                                    
                                    usersPublishedJobs.removeValue().addOnSuccessListener {
                                        


                                        val queryHistoryJob = database.reference.child("Users").orderByChild("role").equalTo("worker")
                                        queryHistoryJob.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()){
                                                    for (childrenSnapshot in snapshot.children){


                                                        if (childrenSnapshot.hasChild("history_job")){

                                                            val keyChildrenSnapshot = childrenSnapshot.key
                                                            Log.d(TAG, "onDataChange: $keyChildrenSnapshot")

                                                            val usersHistoryJob = database.reference.child("Users")
                                                            val queryAgain = database.reference.child("Users").child(keyChildrenSnapshot.toString()).child("history_job").orderByChild("id_job").equalTo(idJob)
                                                            Log.d(TAG, "queryAgain_ref: ${queryAgain.ref}")


                                                            queryAgain.addListenerForSingleValueEvent(object : ValueEventListener {
                                                                override fun onDataChange(snapshot: DataSnapshot) {

                                                                    for (ds in snapshot.children){
                                                                        Log.d(TAG, "ds_key: ${ds.key}")


                                                                        if (ds.exists()){
                                                                            val queryDs = usersHistoryJob.child(keyChildrenSnapshot.toString()).child("history_job").child(ds.key.toString())

                                                                            Log.d(TAG, "queryDs_ref: ${queryDs.ref}")

                                                                            queryDs.removeValue().addOnSuccessListener {

                                                                                _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil menghapus seluruh data pekerjaan")))

                                                                            }.addOnFailureListener {
                                                                                _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                                                                            }
                                                                        }
                                                                    }

                                                                }

                                                                override fun onCancelled(error: DatabaseError) {
                                                                    _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                                                                }

                                                            })


                                                        }

                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                                            }

                                        })
                                        
                                        
                                    }.addOnFailureListener {
                                        _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                                    }


                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                        }

                    })


                }.addOnFailureListener {
                    _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                }

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _deleteJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun getJobStatus(id_job: String?){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Jobs").child(id_job.toString())
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val jobStatusValue = snapshot.child("job_status").getValue(String::class.java).toString()
                        _getJobStatusResponse.postValue(SingleLiveEvent(BaseResponse.Success(jobStatusValue)))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getJobStatusResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getJobStatusResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun chooseApplicant(id_job: String?, id_applicant: String?){
        viewModelScope.launch {
            try {

                val statusUpdates = hashMapOf<String, Any?>(
                    "status" to "accepted"
                )

                val jobStatus = hashMapOf<String, Any?>(
                    "job_status" to "closed"
                )

                // query update job status
                val queryJob = database.reference.child("Jobs").child(id_job.toString())
                queryJob.updateChildren(jobStatus).addOnSuccessListener {


                    // query update status on_review to rejected
                    val queryApplicant = database.reference.child("Jobs").child(id_job.toString()).child("applicant")
                    queryApplicant.orderByChild("status").equalTo("on_review").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children){
                                queryApplicant.child(childSnapshot.key.toString()).child("status").setValue("rejected").addOnSuccessListener {


                                    // query update status rejected to diterima
                                    val ref = database.reference.child("Jobs").child(id_job.toString()).child("applicant").child(id_applicant.toString())
                                    ref.addValueEventListener(object : ValueEventListener{
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val uid_worker = snapshot.child("uid").getValue(String::class.java).toString()

                                            val queryHistoryJob = database.reference.child("Users").orderByChild("role").equalTo("worker")
                                            val dataHistoryList: MutableList<HistoryJob> = ArrayList()
                                            queryHistoryJob.addListenerForSingleValueEvent(object : ValueEventListener{
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    dataHistoryList.clear()
                                                    if (snapshot.exists()){
                                                        for (childSnapshot in snapshot.children){

                                                            if (childSnapshot.hasChild("history_job")){
                                                                val key = childSnapshot.key
                                                                Log.d(TAG, "childSnapshot_key: $key")

                                                                val users = database.reference.child("Users")
                                                                val queryAgain = database.reference.child("Users").child(key.toString()).child("history_job").orderByChild("id_job").equalTo(id_job)
                                                                Log.d(TAG, "query_again_ref: ${queryAgain.ref}")
                                                                queryAgain.addListenerForSingleValueEvent(object : ValueEventListener{
                                                                    override fun onDataChange(
                                                                        snapshot: DataSnapshot
                                                                    ) {
                                                                        for (ds in snapshot.children){
                                                                            Log.d(TAG, "onDataChange: ${ds.key}")
                                                                            if (ds.exists()){
                                                                                val dataQuery = users.child(key.toString()).child("history_job").child(ds.key.toString())
                                                                                dataQuery.child("status").setValue("rejected")
                                                                                Log.d(TAG, "onDataChange: ${dataQuery.ref}")
                                                                                
                                                                            } else {
                                                                                Log.d(
                                                                                    TAG,
                                                                                    "onDataChange: tidak ada"
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    override fun onCancelled(error: DatabaseError) {
                                                                        _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                                                                    }

                                                                })
                                                            }

                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                                                }

                                            })

                                            // query update history job -> status job
//                                            val queryWorker = database.reference.child("Users").child(uid_worker).child("history_job")
//                                            queryWorker.orderByChild("id_job").equalTo(id_job).addListenerForSingleValueEvent(object : ValueEventListener{
//                                                override fun onDataChange(snapshot: DataSnapshot) {
//                                                    Log.d(TAG, "snapshot_query_worker: $snapshot")
//                                                    if (snapshot.exists()){
//                                                        for(childSnapshot in snapshot.children){
//                                                            val key = childSnapshot.key
//                                                            if(key != null){
//                                                                Log.d(TAG, "key: $key")
//                                                                Log.d(TAG, "ref: ${queryWorker.ref}")
//                                                                queryWorker.child(key).child("status").setValue("accepted")
//                                                            }
//                                                        }
//                                                    }
//                                                }
//
//                                                override fun onCancelled(error: DatabaseError) {
//                                                    _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
//                                                }
//
//                                            })


                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                                        }

                                    })
                                    ref.updateChildren(statusUpdates).addOnSuccessListener {
                                        _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil memilih pekerja")))
                                    }.addOnFailureListener {
                                        _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                                    }


                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                        }

                    })

                }.addOnFailureListener {
                    _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                }


            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _chooseApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun getIdApplicant(idJob: String, uid_worker: String){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Jobs").child(idJob).child("applicant").orderByChild("uid").equalTo(uid_worker)
                val idApplicantList: MutableList<String> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        idApplicantList.clear()
                        for (childSnapshot in snapshot.children){
                            val id = childSnapshot.child("id_applicant").getValue(String::class.java).toString()

//                            val applicant: String? = childSnapshot.getValue(Applicant::class.java)?.id_applicant

                            if (id != null){
                                idApplicantList.add(id)
                            }
                            _getIdApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Success(idApplicantList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getIdApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getIdApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun addApplicant(
        uid: String,
        applicant: Applicant?,
        historyJob: HistoryJob?,
        recruiterUid: String?,
        recruiterUsername: String?,
        jobTitle: String?
    ){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Jobs").child(applicant?.id_job.toString()).child("applicant").push()
                applicant?.id_applicant = ref.key
                applicant?.status = "on_review"



                val queryUser = database.reference.child("Users").child(uid.toString()).child("history_job").push()
                historyJob?.job_title = jobTitle
                historyJob?.recruiter_uid = recruiterUid
                historyJob?.recruiter_name = recruiterUsername
                historyJob?.status = "on_review"
                queryUser.setValue(historyJob)
                    .addOnSuccessListener {
                        ref.setValue(applicant)
                            .addOnSuccessListener {
                                _addApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil melamar")))
                            }.addOnFailureListener {
                                _addApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                            }
                    }.addOnFailureListener {
                        _addApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                    }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addApplicantResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun getTotalApplicant(uid: String, id_published_job: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs").child(id_published_job).child("applicant")
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _getTotalApplicantResponse.postValue(BaseResponse.Success(snapshot.childrenCount.toInt()))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getTotalApplicantResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getTotalApplicantResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getPublishedJobByRecruiterUid(uid: String){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Jobs").orderByChild("uid").equalTo(uid)
                val publishedJobList: MutableList<JobDetails> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        publishedJobList.clear()
                        for (childSnapshot in snapshot.children){
                            val publishedJob: JobDetails? = childSnapshot.getValue(JobDetails::class.java)
                            if (publishedJob != null){
                                publishedJobList.add(publishedJob)
                            }
                            _getPublishedJobByRecruiterUidResponse.postValue(SingleLiveEvent(BaseResponse.Success(publishedJobList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getPublishedJobByRecruiterUidResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getPublishedJobByRecruiterUidResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
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

    fun editJob(uid: String, jobDetails: JobDetails){
        viewModelScope.launch {
            try{

                val jobUpdate = hashMapOf<String, Any>(
                    "job_title" to jobDetails.job_title.toString(),
                    "dateStart" to jobDetails.dateStart.toString(),
                    "dateEnd" to jobDetails.dateEnd.toString(),
                    "total_day" to jobDetails.total_day.toString(),
                    "job_description" to jobDetails.job_description.toString(),
                    "job_category" to jobDetails.job_category.toString(),
                    "employee_type" to jobDetails.employee_type.toString(),
                    "job_address" to jobDetails.job_address.toString(),
                    "salary" to jobDetails.salary.toString(),
                    "edited_at" to jobDetails.edited_at.toString()
                )

                val ref = database.reference.child("Jobs").child(jobDetails.id.toString())
                ref.updateChildren(jobUpdate).addOnSuccessListener {
                    _editJobResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil mengubah data")))
                }.addOnFailureListener {
                    _editJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                }

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
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
                        jobDetails.job_status = "open"
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

    fun getDetailJob(id_job: String?){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Jobs").child(id_job.toString())
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val id = snapshot.child("id").getValue(String::class.java).toString()
                        val uid = snapshot.child("uid").getValue(String::class.java).toString()
                        val job_title = snapshot.child("job_title").getValue(String::class.java).toString()
                        val person_who_post = snapshot.child("person_who_post").getValue(String::class.java).toString()
                        val image_url = snapshot.child("image_url").getValue(String::class.java).toString()
                        val dateStart = snapshot.child("dateStart").getValue(String::class.java).toString()
                        val dateEnd = snapshot.child("dateEnd").getValue(String::class.java).toString()
                        val total_day = snapshot.child("total_day").getValue(String::class.java).toString()
                        val job_description = snapshot.child("job_description").getValue(String::class.java).toString()
                        val job_category = snapshot.child("job_category").getValue(String::class.java).toString()
                        val employee_type = snapshot.child("employee_type").getValue(String::class.java).toString()
                        val job_address = snapshot.child("job_address").getValue(String::class.java).toString()
                        val salary = snapshot.child("salary").getValue(String::class.java).toString()
                        val post_time = snapshot.child("post_time").getValue(String::class.java).toString()
                        val edited_at = snapshot.child("edited_at").getValue(String::class.java).toString()
                        val job_status = snapshot.child("job_status").getValue(String::class.java).toString()

                        val job = JobDetails (
                            id = id,
                            uid = uid,
                            job_title = job_title,
                            person_who_post = person_who_post,
                            image_url = image_url,
                            dateStart = dateStart,
                            dateEnd = dateEnd,
                            total_day = total_day,
                            job_description = job_description,
                            job_category = job_category,
                            employee_type = employee_type,
                            job_address = job_address,
                            salary = salary,
                            post_time = post_time,
                            edited_at = edited_at,
                            job_status = job_status
                        )

                        _getDetailJobResponse.postValue(SingleLiveEvent(BaseResponse.Success(job)))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getDetailJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getDetailJobResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
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