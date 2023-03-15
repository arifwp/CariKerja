package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.education

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.EducationDetails
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
class EducationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "EducationViewModel"

    private val _addEducationResponse = MutableLiveData<BaseResponse<String>>()
    val addEducationResponse: LiveData<BaseResponse<String>> = _addEducationResponse

    private val _getEducationResponse = MutableLiveData<SingleLiveEvent<BaseResponse<MutableList<EducationDetails>>>>()
    val getEducationResponse: LiveData<SingleLiveEvent<BaseResponse<MutableList<EducationDetails>>>> = _getEducationResponse

    private val _editEducationResponse = MutableLiveData<BaseResponse<String>>()
    val editEducationResponse: LiveData<BaseResponse<String>> = _editEducationResponse

    private val _deleteEducationResponse = MutableLiveData<BaseResponse<String>>()
    val deleteEducationResponse: LiveData<BaseResponse<String>> = _deleteEducationResponse

    fun deleteEducation(uid: String, id: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("education").child(id)
                ref.removeValue().addOnSuccessListener {
                    _editEducationResponse.postValue(BaseResponse.Success("Berhasil menghapus data"))
                }.addOnFailureListener {
                    _editEducationResponse.postValue(BaseResponse.Error(it.message.toString()))
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editEducationResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun editEducation(
        uid: String,
        id: String,
        institution: String,
        degree: String,
        fieldStudy: String,
        dateStart: String,
        dateEnd: String,
        description: String
    ){
        viewModelScope.launch {
            try {
                val childUpdates = hashMapOf<String, Any>(
                    "institution" to institution,
                    "degree" to degree,
                    "field_of_study" to fieldStudy,
                    "dateStart" to dateStart,
                    "dateEnd" to dateEnd,
                    "description" to description
                )

                val ref = database.reference.child("Users").child(uid.toString()).child("education").child(id)
                ref.updateChildren(childUpdates).addOnSuccessListener {
                    _editEducationResponse.postValue(BaseResponse.Success("Berhasil mengubah data"))
                }.addOnFailureListener {
                    _editEducationResponse.postValue(BaseResponse.Error(it.message.toString()))
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editEducationResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getEducation(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("education")
                val dataEduList: MutableList<EducationDetails> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataEduList.clear()
                        for (childSnapshot in snapshot.children){
                            val educationDetails: EducationDetails? = childSnapshot.getValue(EducationDetails::class.java)
                            if (educationDetails != null) {
                                dataEduList.add(educationDetails)
                            }
                            _getEducationResponse.postValue(SingleLiveEvent(BaseResponse.Success(dataEduList)))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getEducationResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message)))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getEducationResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun addEducation(uid: String, dataEdu: EducationDetails){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("education").push()
                dataEdu.id = ref.key
                ref.setValue(dataEdu).addOnSuccessListener {
                    _addEducationResponse.postValue(BaseResponse.Success("Berhasil menambahkan data"))
                }.addOnFailureListener {
                    _addEducationResponse.postValue(BaseResponse.Error(it.message.toString()))
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addEducationResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}