package com.amikom.carikerja.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.UserDetails
import com.amikom.carikerja.utils.AllEvents
import com.amikom.carikerja.utils.SingleLiveEvent
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val rootNode: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "AuthenticationViewModel"
    var authCurrentUser = FirebaseAuth.getInstance().currentUser
    private var storageReference: StorageReference = storage.reference
    private lateinit var reference: DatabaseReference

    private var uid: String? = null
    private var userEmail: String? = null
    private var userPassword: String? = null
    var userDetails: UserDetails = UserDetails()

    private val _signInStatus = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val signInStatus: LiveData<SingleLiveEvent<BaseResponse<String>>> = _signInStatus

    private val _registerResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val registerResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _registerResponse

    private val _forgotPasswordResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val forgotPasswordResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _forgotPasswordResponse

    private val _logoutResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val logoutResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _logoutResponse

    fun login(email: String, password: String){
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            val uidLogin = it.result.user?.uid.toString()
                            _signInStatus.postValue(SingleLiveEvent(BaseResponse.Success(uidLogin)))
                        } else {
                            val error = it.exception.toString().split(":").toTypedArray()
                            _signInStatus.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
                        }
                    }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _signInStatus.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch() {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            userEmail = email
                            userPassword = password
                            uid = it.result.user?.uid.toString()
                            saveUserDetails(userDetails = userDetails)
                        } else {
                            val error = it.exception.toString().split(":").toTypedArray()
                            _registerResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
                        }
                    }
            } catch(e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _registerResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    private fun saveUserDetails(userDetails: UserDetails) {
        viewModelScope.launch {
            try {
                reference = FirebaseDatabase.getInstance().getReference("Users")
                val user = UserDetails(
                    uid = uid,
                    name = userDetails.name,
                    email = userDetails.email,
                    phone = userDetails.phone,
                    password = userDetails.password
                )
                reference.child(uid.toString()).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful){
                        _registerResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil register")))
                    } else {
                        deleteCurrentUser()
                    }
                }
            } catch(e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _registerResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun deleteCurrentUser() {
        viewModelScope.launch {
            try {
                val authCredential = EmailAuthProvider.getCredential(
                    userEmail.toString(),
                    userPassword.toString()
                )
                authCurrentUser?.reauthenticate(authCredential)?.addOnCompleteListener {
                    if (it.isSuccessful){
                        authCurrentUser?.delete()?.addOnCompleteListener { delete ->
                            if (delete.isSuccessful){

                            } else {
                                val error = delete.exception.toString().split(":").toTypedArray()
                                _registerResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
                            }
                        }
                    }
                }
            } catch(e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _registerResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful){
                        _forgotPasswordResponse.postValue(SingleLiveEvent(BaseResponse.Success("Email telah dikirim")))
                    } else {
                        val error = it.exception.toString().split(":").toTypedArray()
                        _forgotPasswordResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
                    }
                }
            } catch(e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _forgotPasswordResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            try {
                auth.signOut()
                _logoutResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil keluar")))
            } catch(e: Exception){
                val error = e.toString().split(":").toTypedArray()
                _logoutResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }


}