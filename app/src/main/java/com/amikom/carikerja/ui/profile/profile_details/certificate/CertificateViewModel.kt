package com.amikom.carikerja.ui.profile.profile_details.certificate

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.CertificateDetailString
import com.amikom.carikerja.models.CertificateDetails
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
class CertificateViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "CertificateViewModel"
    var urlImg: Uri? = null

    private val _addCertificateResponse = MutableLiveData<BaseResponse<String>>()
    val addCertificateResponse: LiveData<BaseResponse<String>> = _addCertificateResponse

    private val _getCertificateResponse = MutableLiveData<BaseResponse<MutableList<CertificateDetailString>>>()
    val getCertificateResponse: LiveData<BaseResponse<MutableList<CertificateDetailString>>> = _getCertificateResponse

    private val _editCertificateResponse = MutableLiveData<BaseResponse<String>>()
    val editCertificateResponse: LiveData<BaseResponse<String>> = _editCertificateResponse

    private val _deleteCertificateResponse = MutableLiveData<BaseResponse<String>>()
    val deleteCertificateResponse: LiveData<BaseResponse<String>> = _deleteCertificateResponse

    fun deleteCertificate(uid: String, id: String, imageUrl: String){
        viewModelScope.launch {
            try {
                val storageReference = storage.getReferenceFromUrl(imageUrl)
                storageReference.delete().addOnSuccessListener {
                    val ref = database.reference.child("Users").child(uid).child("certificate").child(id)
                    ref.removeValue().addOnSuccessListener {
                        _deleteCertificateResponse.postValue(BaseResponse.Success("Berhasil menghapus data"))
                    }.addOnFailureListener {
                        _deleteCertificateResponse.postValue(BaseResponse.Error(it.message.toString()))
                    }
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _deleteCertificateResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun editCertificate(
        uid: String,
        id: String,
        certificate_name: String,
        publishing_organization: String,
        dateStart: String,
        expiration_date: String,
        credential_id: String,
        credential_url: String,
        imageUrl: String,
        image: Uri?
    ){
        viewModelScope.launch {
            try {
                if (image != null){
                    val referenceStorage = storage.getReferenceFromUrl(imageUrl)
                    referenceStorage.putFile(image).addOnSuccessListener {
                        referenceStorage.downloadUrl.addOnSuccessListener { url ->
                            val childUpdates = hashMapOf<String, Any>(
                                "title" to certificate_name,
                                "publishing_organization" to publishing_organization,
                                "dateStart" to dateStart,
                                "expiration_date" to expiration_date,
                                "credential_id" to credential_id,
                                "credential_url" to credential_url,
                                "image" to url.toString()
                            )
                            database.reference.child("Users").child(uid).child("certificate").child(id).updateChildren(childUpdates).addOnSuccessListener {
                                _editCertificateResponse.postValue(BaseResponse.Success("Berhasil memperbarui data"))
                            }.addOnFailureListener {
                                _editCertificateResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }
                        }
                    }
                } else {
                    val childUpdates = hashMapOf<String, Any>(
                        "title" to certificate_name,
                        "publishing_organization" to publishing_organization,
                        "dateStart" to dateStart,
                        "expiration_date" to expiration_date,
                        "credential_id" to credential_id,
                        "credential_url" to credential_url,
                        "image" to "null"
                    )
                    database.reference.child("Users").child(uid).child("certificate").child(id).updateChildren(childUpdates).addOnSuccessListener {
                        Log.d(TAG, "editCertificate: $id")
                        _editCertificateResponse.postValue(BaseResponse.Success("Berhhasil mengubah data"))
                    }.addOnFailureListener {
                        _editCertificateResponse.postValue(BaseResponse.Error(it.message.toString()))
                    }
                }
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _editCertificateResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getCertificate(uid: String){
        viewModelScope.launch {
            try {
                val ref = database.reference.child("Users").child(uid).child("certificate")
                val certificateList: MutableList<CertificateDetailString> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        certificateList.clear()
                        for (childSnapshot in snapshot.children){
                            val certificateDetails: CertificateDetailString? = childSnapshot.getValue(CertificateDetailString::class.java)
                            if (certificateDetails != null){
                                certificateList.add(certificateDetails)
                            }
                            _getCertificateResponse.postValue(BaseResponse.Success(certificateList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getCertificateResponse.postValue(BaseResponse.Error(error.message))
                    }

                })
            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getCertificateResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun addCertificate(uid: String, certificate: CertificateDetails){
        viewModelScope.launch {
            try {

                if (certificate.image != null){
                    val referenceStorage = storage.reference.child("Certificate").child("${uid}-${Date().time.toString()}")
                    certificate.image?.let { referenceStorage.putFile(it) }?.addOnSuccessListener {
                        referenceStorage.downloadUrl.addOnSuccessListener { url ->

                            val query = database.reference.child("Users").child(uid).child("certificate").push()
                            val newCertificate = CertificateDetailString(
                                id = query.key,
                                title = certificate.title,
                                publishing_organization = certificate.publishing_organization,
                                dateStart = certificate.dateStart,
                                expiration_date = certificate.expiration_date,
                                credential_id = certificate.credential_id,
                                credential_url = certificate.credential_url,
                                image = url.toString()
                            )

                            query.setValue(newCertificate).addOnSuccessListener {
                                _addCertificateResponse.postValue(BaseResponse.Success("Berhasil menambahkan data"))
                            }.addOnFailureListener {
                                _addCertificateResponse.postValue(BaseResponse.Error(it.message.toString()))
                            }.addOnCanceledListener {
                                _addCertificateResponse.postValue(BaseResponse.Error(it.error.toString()))
                            }

                        }.addOnFailureListener {
                            _addCertificateResponse.postValue(BaseResponse.Error(it.message.toString()))
                        }
                    }?.addOnFailureListener{
                        _addCertificateResponse.postValue(BaseResponse.Error(it.message.toString()))
                    }

                } else {
                    val ref = database.reference.child("Users").child(uid).child("certificate").push()
                    val newCertificate = CertificateDetailString(
                        id = ref.key,
                        title = certificate.title,
                        publishing_organization = certificate.publishing_organization,
                        dateStart = certificate.dateStart,
                        expiration_date = certificate.expiration_date,
                        credential_id = certificate.credential_id,
                        credential_url = certificate.credential_url,
                        image = "null"
                    )

                    ref.setValue(newCertificate).addOnSuccessListener {
                        _addCertificateResponse.postValue(BaseResponse.Success("Berhasil menambahkan data"))
                    }.addOnFailureListener {
                        _addCertificateResponse.postValue(BaseResponse.Error(it.message.toString()))
                    }
                }


            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _addCertificateResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}