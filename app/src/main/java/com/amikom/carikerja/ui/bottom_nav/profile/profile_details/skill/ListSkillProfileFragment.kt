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
import com.amikom.carikerja.adapter.ListSkillProfileAdapter
import com.amikom.carikerja.adapter.chooseDeleteSkillsListener
import com.amikom.carikerja.databinding.FragmentListSkillProfileBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.SkillsDetail
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListSkillProfileFragment : Fragment(), chooseDeleteSkillsListener {

    private var _binding: FragmentListSkillProfileBinding? = null
    private val binding get() = _binding!!
    private val listSkillProfileViewModel: ListSkillProfileViewModel by viewModels()
    private lateinit var listSkillProfileAdapter: ListSkillProfileAdapter
    private var uid: String? = null
    private val TAG = "SkillProfileFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListSkillProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())

        val icBack = binding.icBack
        icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val btnEdit = binding.btnEdit
        btnEdit.setOnClickListener {
            findNavController().navigate(ListSkillProfileFragmentDirections.actionListSkillProfileFragmentToAddListSkillProfileFragment())
        }

        listSkillProfileViewModel.getUserSkills(uid.toString())
        observe()
        initiateRv()
    }

    override fun btnOnDeleteSkills(data: ArrayList<SkillsDetail>, selectedName: String?) {

        if (data.size < 1) {
            binding.wrapBtnSubmit.visibility = View.GONE
        } else if (data.size > 1) {
            textMessage("Hanya bisa memilih 1 keahlian")
            binding.wrapBtnSubmit.visibility = View.GONE
        } else if (data.size > 0){
            binding.wrapBtnSubmit.visibility = View.VISIBLE
            binding.btnSubmit.setOnClickListener {
                textMessage(selectedName.toString())
                Log.d(TAG, "btnOnDeleteSkills: ${selectedName.toString()}")
                if (selectedName != null){
                    listSkillProfileViewModel.deleteSkills(uid.toString(), selectedName.toString())
                }
            }
        }

    }

    private fun observe() {
        listSkillProfileViewModel.getSkillsResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    listSkillProfileAdapter.setSkillsData(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        listSkillProfileViewModel.deleteSkillsResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun initiateRv() {
        val recyclerViewListSkill: RecyclerView = requireView().findViewById(R.id.rv_skill_profile)
        listSkillProfileAdapter = ListSkillProfileAdapter(ArrayList())
        listSkillProfileAdapter.listenerDeleteSkills = this
        recyclerViewListSkill.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = listSkillProfileAdapter
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