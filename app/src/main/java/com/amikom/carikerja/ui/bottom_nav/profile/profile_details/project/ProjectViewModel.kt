package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.project

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.ProjectDetails
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
class ProjectViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "ProjectViewModel"

    private val _addProjectResponse = MutableLiveData<BaseResponse<String>>()
    val addProjectResponse: LiveData<BaseResponse<String>> = _addProjectResponse

    private val _getProjectResponse = MutableLiveData<BaseResponse<MutableList<ProjectDetails>>>()
    val getProjectResponse: LiveData<BaseResponse<MutableList<ProjectDetails>>> = _getProjectResponse

    private val _editProjectResponse = MutableLiveData<BaseResponse<String>>()
    val editProjectResponse: LiveData<BaseResponse<String>> = _editProjectResponse

    private val _deleteProjectResponse = MutableLiveData<BaseResponse<String>>()
    val deleteProjectResponse: LiveData<BaseResponse<String>> = _deleteProjectResponse

    fun deleteProject(
        uid: String,
        id: String
    ){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid.toString()).child("project").child(id)
                ref.removeValue().addOnSuccessListener {
                    _deleteProjectResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                }.addOnFailureListener {
                    _deleteProjectResponse.postValue(BaseResponse.Error(it.message.toString()))
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _deleteProjectResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun editProject(
        uid: String,
        id: String,
        projectName: String,
        role: String,
        dateStart: String,
        dateEnd: String,
        desc: String,
    ){
        viewModelScope.launch {
            try {
                val childUpdates = hashMapOf<String, Any>(
                    "project_name" to projectName,
                    "role" to role,
                    "dateStart" to dateStart,
                    "dateEnd" to dateEnd,
                    "description" to desc
                )

                val ref = database.reference.child("Users").child(uid).child("project").child(id)
                ref.updateChildren(childUpdates).addOnSuccessListener {
                    _editProjectResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                }.addOnFailureListener {
                    _editProjectResponse.postValue(BaseResponse.Error(it.message.toString()))
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editProjectResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getProject(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("project")
                val dataProjectList: MutableList<ProjectDetails> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataProjectList.clear()
                        for (childSnapshot in snapshot.children){
                            val projectDetails: ProjectDetails? = childSnapshot.getValue(ProjectDetails::class.java)
                            if (projectDetails != null){
                                dataProjectList.add(projectDetails)
                            }
                            _getProjectResponse.postValue(BaseResponse.Success(dataProjectList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getProjectResponse.postValue(BaseResponse.Error(error.message))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getProjectResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun addProject(uid: String, dataProject: ProjectDetails){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("project").push()
                dataProject.id = ref.key
                ref.setValue(dataProject).addOnSuccessListener {
                    _addProjectResponse.postValue(BaseResponse.Success("Berhasil menambahkan data"))
                }.addOnFailureListener {
                    _addProjectResponse.postValue(BaseResponse.Error(it.message.toString()))
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addProjectResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}