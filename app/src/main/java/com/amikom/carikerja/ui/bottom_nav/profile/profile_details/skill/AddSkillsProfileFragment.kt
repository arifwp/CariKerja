package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.skill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.AddSkillsProfileAdapter
import com.amikom.carikerja.adapter.ListSkillProfileAdapter
import com.amikom.carikerja.adapter.SkillsAdapter
import com.amikom.carikerja.adapter.btnAddSkillsListener
import com.amikom.carikerja.databinding.FragmentAddSkillsProfileBinding
import com.amikom.carikerja.databinding.FragmentListSkillProfileBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.SkillsDetail
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddSkillsProfileFragment : Fragment(), btnAddSkillsListener {

    private var _binding: FragmentAddSkillsProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var addSkillsProfileAdapter: AddSkillsProfileAdapter
    private var uid: String? = null
    private val TAG = "AddSkillsFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddSkillsProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getSkill(uid.toString())
        
        val icBack = binding.icBack
        icBack.setOnClickListener { 
            findNavController().popBackStack()
        }
        
        observe()
        initiateRv()
    }

    override fun btnAddSkills(data: ArrayList<SkillsDetail>) {
        Log.d(TAG, "btnAddSkills: $data")
        if (data.size < 1){
            binding.wrapBtnSubmit.visibility = View.GONE
        } else if (data.size > 0){
            binding.wrapBtnSubmit.visibility = View.VISIBLE
            binding.btnSubmit.setOnClickListener {
                profileViewModel.insertSkill(uid.toString(), data)
            }
        }
    }

    private fun observe() {
        profileViewModel.getSkillResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let { 
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        addSkillsProfileAdapter.setAddDataSkills(it.data)
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }
        
        profileViewModel.insertSkillResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    findNavController().popBackStack()
                    textMessage(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun initiateRv() {
        val recyclerViewSkills: RecyclerView = requireView().findViewById(R.id.rv_add_skill)
        addSkillsProfileAdapter = AddSkillsProfileAdapter(ArrayList())
        addSkillsProfileAdapter.listenerAddSkillsListener = this
        recyclerViewSkills.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = addSkillsProfileAdapter
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}