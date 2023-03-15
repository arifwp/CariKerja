package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.biodata

import android.net.Uri
import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BiodataViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val _editProfileResponse = MutableLiveData<BaseResponse<Any>>()
    val editProfileResponse: LiveData<BaseResponse<Any>> = _editProfileResponse

    fun getProfile(uid: String){
        viewModelScope.launch {
            try {

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editProfileResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun editProfile(uid: String, imageUrl: Uri?, name: String, dob: String, address: String){
        viewModelScope.launch {
            try {

                val nameUpdates = hashMapOf<String, Any>(
                    "name" to name
                )
                database.reference.child("Users").child(uid).updateChildren(nameUpdates)
                    .addOnFailureListener {
                        _editProfileResponse.postValue(BaseResponse.Error("Gagal mengubah nama"))
                    }

                val ref = database.reference.child("Users").child(uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.hasChild("address")){
                            val addressUpdates = hashMapOf<String, Any>(
                                "address" to address
                            )
                            database.reference.child("Users").child(uid).updateChildren(addressUpdates)
                                .addOnFailureListener {
                                    _editProfileResponse.postValue(BaseResponse.Error("Gagal mengubah alamat"))
                                }
                        } else {
                            database.reference.child("Users").child(uid).child("address").setValue(address)
                                .addOnFailureListener {
                                    _editProfileResponse.postValue(BaseResponse.Error("Gagal menambahkan alamat"))
                                }
                        }


                        if (snapshot.hasChild("dob")){
                            val childUpdates = hashMapOf<String, Any>(
                                "dob" to dob
                            )
                            database.reference.child("Users").child(uid).updateChildren(childUpdates)
                                .addOnFailureListener {
                                    _editProfileResponse.postValue(BaseResponse.Error("Gagal mengubah tanggal lahir"))
                                }
                        } else {
                            database.reference.child("Users").child(uid).child("dob").setValue(dob)
                                .addOnFailureListener {
                                    _editProfileResponse.postValue(BaseResponse.Error("Gagal mengubah tanggal lahir"))
                                }
                        }

                        if (snapshot.hasChild("imageUrl")){
                            val referenceStorage = storage.reference.child("Profile").child(Date().time.toString())
                            if (imageUrl != null) {
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
                            }

                        } else {

                            val referenceStorage = storage.reference.child("Profile").child(Date().time.toString())
                            if (imageUrl != null) {
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