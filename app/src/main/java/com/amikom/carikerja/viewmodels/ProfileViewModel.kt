package com.amikom.carikerja.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.amikom.carikerja.models.*
import com.amikom.carikerja.utils.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "ProfileViewModel"
    private var dataWorkExp: MutableList<Exp>? = null

    private val _updateRoleResponse = MutableLiveData<BaseResponse<String>>()
    val updateRoleResponse: LiveData<BaseResponse<String>> = _updateRoleResponse

    private val _checkRoleResponse = MutableLiveData<BaseResponse<String>>()
    val checkRoleResponse: LiveData<BaseResponse<String>> = _checkRoleResponse

    private val _checkSkillsResponse = MutableLiveData<BaseResponse<Boolean>>()
    val checkSkillsResponse: LiveData<BaseResponse<Boolean>> = _checkSkillsResponse

    private val _checkProfileCompletenessResponse = MutableLiveData<BaseResponse<String>>()
    val checkProfileCompletenessResponse: LiveData<BaseResponse<String>> = _checkProfileCompletenessResponse

    private val _getProfileResponse = MutableLiveData<SingleLiveEvent<BaseResponse<UserProfile>>>()
    val getProfileResponse: MutableLiveData<SingleLiveEvent<BaseResponse<UserProfile>>> = _getProfileResponse

    private val _insertSkillResponse = MutableLiveData<BaseResponse<String>>()
    val insertSkillResponse: LiveData<BaseResponse<String>> = _insertSkillResponse

    private val _getSkillResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<JobCategory>>>>()
    val getSkillResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<JobCategory>>>> = _getSkillResponse

    private val _getRoleResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>?>()
    val getRoleResponse: MutableLiveData<SingleLiveEvent<BaseResponse<String>>?> = _getRoleResponse

    private val _getUserSkillsResponse = MutableLiveData<BaseResponse<MutableList<SkillsDetail>>>()
    val getUserSkillsResponse: LiveData<BaseResponse<MutableList<SkillsDetail>>> = _getUserSkillsResponse

    fun getUserSkills(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("skills")
                val dataSkillsList: MutableList<SkillsDetail> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataSkillsList.clear()
                        for (childSnapshot in snapshot.children){
                            val data: SkillsDetail? = childSnapshot.getValue(SkillsDetail::class.java)
                            if (data != null) {
                                dataSkillsList.add(data)
                            }
                            _getUserSkillsResponse.postValue(BaseResponse.Success(dataSkillsList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getUserSkillsResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getUserSkillsResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getRole(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid)
                ref.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val role = snapshot.child("role").getValue(String::class.java).toString()
                        _getRoleResponse.postValue(SingleLiveEvent(BaseResponse.Success(role)))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getRoleResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getRoleResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun getSkill(uid: String){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Job Category").orderByChild("name")
                val jobCategoryList: MutableList<JobCategory> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        jobCategoryList.clear()
                        for (childSnapshot in snapshot.children){
                            val jobCategory: JobCategory? = childSnapshot.getValue(JobCategory::class.java)
                            if (jobCategory != null){
                                jobCategoryList.add(jobCategory)
                            }
                            _getSkillResponse.postValue(SingleLiveEvent(BaseResponse.Success(jobCategoryList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getSkillResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getSkillResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun insertSkill(uid: String, skills: ArrayList<SkillsDetail>){
        viewModelScope.launch {
            try {
                database.reference.child("Users").child(uid).child("skills").setValue(skills)
                    .addOnSuccessListener {
                        _insertSkillResponse.postValue(BaseResponse.Success("Berhasil menambahkan keahlian"))
                    }.addOnFailureListener {
                        _insertSkillResponse.postValue(BaseResponse.Error(it.message.toString()))
                    }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _insertSkillResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun updateRole(uid: String, role: String){
        viewModelScope.launch {
            try {
                database.reference.child("Users").child(uid).child("role").setValue(role)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            _updateRoleResponse.postValue(BaseResponse.Success("Profil anda berhasil diperbarui"))
                        } else {
                            val error = task.exception.toString().split(":").toTypedArray()
                            _updateRoleResponse.postValue(BaseResponse.Error(error[1]))
                        }
                    }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _updateRoleResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun checkRole(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("role")){
                            val role = snapshot.child("role").getValue(String::class.java).toString()
                            _checkRoleResponse.postValue(BaseResponse.Success(role))
                        } else {
                            _checkRoleResponse.postValue(BaseResponse.Success(""))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        val error = error.toString().split(":").toTypedArray()
                        _checkRoleResponse.postValue(BaseResponse.Error(error[1]))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _checkRoleResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun checkSkills(uid: String){
        viewModelScope.launch{
            try {
                val ref = database.reference.child("Users").child(uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("skills")){
                            _checkSkillsResponse.postValue(BaseResponse.Success(true))
                        } else {
                            _checkSkillsResponse.postValue(BaseResponse.Success(false))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        val error = error.toString().split(":").toTypedArray()
                        _checkSkillsResponse.postValue(BaseResponse.Error(error[1]))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _checkSkillsResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getProfile(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid)
                val dataProjectList: MutableList<ProjectDetails> = ArrayList()

                ref.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val phone = snapshot.child("phone").getValue(String::class.java).toString()
                        val name = snapshot.child("name").getValue(String::class.java).toString()
                        val email = snapshot.child("email").getValue(String::class.java).toString()
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java).toString()
                        val role = snapshot.child("role").getValue(String::class.java).toString()
                        val dob = snapshot.child("dob").getValue(String::class.java).toString()
                        val address = snapshot.child("address").getValue(String::class.java).toString()
                        val summary = snapshot.child("summary").getValue(String::class.java).toString()
                        val bank_name = snapshot.child("bank_name").getValue(String::class.java).toString()
                        val bank_account = snapshot.child("bank_account").getValue(String::class.java).toString()
                        val bank_account_name = snapshot.child("bank_account_name").getValue(String::class.java).toString()
                        val workExp = snapshot.child("work_experience").getValue(Exp::class.java)
                        val certificate = snapshot.child("certificate").getValue(CertificateDetails::class.java)
                        val project = snapshot.child("project").getValue(ProjectDetails::class.java)
                        val education = snapshot.child("education").getValue(EducationDetails::class.java)

                        val userProfile = UserProfile(
                            uid = uid,
                            imageUrl = imageUrl,
                            name = name,
                            email = email,
                            role = role,
                            phone = phone,
                            dob = dob,
                            address = address,
                            summary = summary,
                            bank_name = bank_name,
                            bank_account = bank_account,
                            bank_account_name = bank_account_name
                        )

                        _getProfileResponse.postValue(SingleLiveEvent(BaseResponse.Success(userProfile)))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getProfileResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getProfileResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

}
