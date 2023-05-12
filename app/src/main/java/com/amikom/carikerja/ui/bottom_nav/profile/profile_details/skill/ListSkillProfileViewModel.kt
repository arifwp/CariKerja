 package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.skill

import android.util.Log
import androidx.lifecycle.*
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.SkillsDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListSkillProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val database: FirebaseDatabase
) : ViewModel(), LifecycleObserver {

    private val TAG = "SkillProfileViewModel"

    private val _getSkillsResponse = MutableLiveData<BaseResponse<MutableList<SkillsDetail>>>()
    val getSkillsResponse: LiveData<BaseResponse<MutableList<SkillsDetail>>> = _getSkillsResponse

    private val _deleteSkillsResponse = MutableLiveData<BaseResponse<String>>()
    val deleteSkillsResponse: LiveData<BaseResponse<String>> = _deleteSkillsResponse

    fun deleteSkills(uid: String, name: String){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Users").child(uid).child("skills").orderByChild("skill_name").equalTo(name)
                ref.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (childSnapshot in snapshot.children){
                            Log.d(TAG, "onDataChange: ${snapshot.ref}")
                            childSnapshot.ref.removeValue()
                            _deleteSkillsResponse.postValue(BaseResponse.Success("Berhasil menghapus"))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _deleteSkillsResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _deleteSkillsResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

    fun getUserSkills(uid: String){
        viewModelScope.launch {
            try {

                val ref = database.reference.child("Users").child(uid).child("skills")
                val skillsList: MutableList<SkillsDetail> = ArrayList()
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        skillsList.clear()
                        for (childSnapshot in snapshot.children){
                            val skills: SkillsDetail? = childSnapshot.getValue(SkillsDetail::class.java)
                            if (skills != null){
                                skillsList.add(skills)
                            }
                            _getSkillsResponse.postValue(BaseResponse.Success(skillsList))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _getSkillsResponse.postValue(BaseResponse.Error(error.message.toString()))
                    }

                })

            } catch (e: java.lang.Exception) {
                val error = e.toString().split(":").toTypedArray()
                _getSkillsResponse.postValue(BaseResponse.Error(error[1]))
            }
        }
    }

}