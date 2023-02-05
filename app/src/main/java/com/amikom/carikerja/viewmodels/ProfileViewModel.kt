package com.amikom.carikerja.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.UserProfile
import com.amikom.carikerja.utils.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "ProfileViewModel"
    private var storageReference: StorageReference = storage.reference
    private lateinit var reference: DatabaseReference
    var authCurrentUser = FirebaseAuth.getInstance().currentUser

    private val _updateRoleResponse = MutableLiveData<BaseResponse<String>>()
    val updateRoleResponse: LiveData<BaseResponse<String>> = _updateRoleResponse

    private val _checkRoleResponse = MutableLiveData<BaseResponse<String>>()
    val checkRoleResponse: LiveData<BaseResponse<String>> = _checkRoleResponse

    private val _checkProfileCompletenessResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val checkProfileCompletenessResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _checkProfileCompletenessResponse

    private val _getProfileResponse = MutableLiveData<BaseResponse<UserProfile>>()
    val getProfileResponse: LiveData<BaseResponse<UserProfile>> = _getProfileResponse

    private val _editProfileResponse = MutableLiveData<BaseResponse<Any>>()
    val editProfileResponse: LiveData<BaseResponse<Any>> = _editProfileResponse

    fun updateRole(uid: String, role: String){
        viewModelScope.launch {
            try {
                database.getReference().child("Users").child(uid).child("role").setValue(role)
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
                            _checkRoleResponse.postValue(BaseResponse.Success("exist"))
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

    fun checkProfileCompleteness(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("avatar")){
                            _checkProfileCompletenessResponse.postValue(SingleLiveEvent(BaseResponse.Success("avatar_exists")))
                        } else if (snapshot.hasChild("bod")){
                            _checkProfileCompletenessResponse.postValue(SingleLiveEvent(BaseResponse.Success("bod_exists")))
                        } else if (snapshot.hasChild("address")){
                            _checkProfileCompletenessResponse.postValue(SingleLiveEvent(BaseResponse.Success("address_exists")))
                        } else {
                            _checkProfileCompletenessResponse.postValue(SingleLiveEvent(BaseResponse.Success("")))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _checkProfileCompletenessResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun getProfile(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid)
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
                        )

                        _getProfileResponse.postValue(BaseResponse.Success(userProfile))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getProfileResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getProfileResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun editProfile(uid: String, imageUrl: Uri){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.hasChild("imageUrl")){
                            val referenceStorage = storage.reference.child("Profile").child(Date().time.toString())
                            referenceStorage.putFile(imageUrl).addOnSuccessListener {
                                referenceStorage.downloadUrl.addOnSuccessListener { url ->
                                    val childUpdates = hashMapOf<String, Any>(
                                        "imageUrl" to url.toString()
                                    )
                                    database.reference.child("Users").child(uid).updateChildren(childUpdates)
                                    _editProfileResponse.postValue(BaseResponse.Success("Profil anda berhasil diperbarui"))
                                }.addOnFailureListener {
                                    _editProfileResponse.postValue(BaseResponse.Error(it.message.toString()))
                                }
                            }.addOnFailureListener{
                                _editProfileResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }

                        } else {

                            val referenceStorage = storage.reference.child("Profile").child(Date().time.toString())
                            referenceStorage.putFile(imageUrl).addOnSuccessListener {
                                referenceStorage.downloadUrl.addOnSuccessListener {
                                    database.reference.child("Users").child(uid).child("imageUrl").setValue(it.toString())
                                    _editProfileResponse.postValue(BaseResponse.Success("Profil anda berhasil diperbarui"))
                                }.addOnFailureListener {
                                    _editProfileResponse.postValue(BaseResponse.Error(it.message.toString()))
                                }
                            }.addOnFailureListener {
                                _editProfileResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _editProfileResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editProfileResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}