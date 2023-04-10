package com.amikom.carikerja.viewmodels

import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.UserDetails
import com.amikom.carikerja.utils.SingleLiveEvent
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
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

    private val _saveNewTokenResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val saveNewTokenResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _saveNewTokenResponse

    private val _changePasswordResponse = MutableLiveData<SingleLiveEvent<BaseResponse<String>>>()
    val changePasswordResponse: LiveData<SingleLiveEvent<BaseResponse<String>>> = _changePasswordResponse

    fun changePassword(uid: String, old_password: String, new_password: String){
        viewModelScope.launch {
            try {

                val passwordUpdate = hashMapOf<String, Any>(
                    "password" to new_password
                )


                val queryUpdateAuth = database.reference.child("Users").child(uid)
                queryUpdateAuth.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val email = snapshot.child("email").getValue(String::class.java).toString()
                        val password = snapshot.child("password").getValue(String::class.java).toString()

                        val credential = EmailAuthProvider.getCredential(email, password)

                        if (old_password != password){
                            _changePasswordResponse.postValue(SingleLiveEvent(BaseResponse.Success("Kata sandi lama anda tidak cocok")))
                        } else {

                            authCurrentUser?.reauthenticate(credential)?.addOnSuccessListener {

                                    authCurrentUser?.updatePassword(new_password)?.addOnSuccessListener {

                                        val queryUpdateDatabase = database.reference.child("Users").child(uid)
                                        queryUpdateDatabase.updateChildren(passwordUpdate).addOnSuccessListener {
                                            _changePasswordResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil memperbarui kata sandi")))
                                        }.addOnFailureListener {
                                            _changePasswordResponse.postValue(SingleLiveEvent(BaseResponse.Success(it.message.toString())))
                                        }

                                    }?.addOnFailureListener {
                                        _changePasswordResponse.postValue(SingleLiveEvent(BaseResponse.Success(it.message.toString())))
                                    }

                                }?.addOnFailureListener {
                                _changePasswordResponse.postValue(SingleLiveEvent(BaseResponse.Success(it.message.toString())))
                                }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _changePasswordResponse.postValue(SingleLiveEvent(BaseResponse.Error(error.message.toString())))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _changePasswordResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

    fun saveNewToken(uid: String, token: String){
        viewModelScope.launch {
            try {

                database.reference.child("Users").child(uid).child("registration_id").setValue(token).addOnSuccessListener {
                    _saveNewTokenResponse.postValue(SingleLiveEvent(BaseResponse.Success("Berhasil")))
                }.addOnFailureListener {
                    _saveNewTokenResponse.postValue(SingleLiveEvent(BaseResponse.Error(it.message.toString())))
                }

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _saveNewTokenResponse.postValue(SingleLiveEvent(BaseResponse.Error(error[1])))
            }
        }
    }

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