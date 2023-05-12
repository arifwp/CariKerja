package com.amikom.carikerja.ui.bottom_nav.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentChooseRoleBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseRoleFragment : Fragment() {

    private var _binding: FragmentChooseRoleBinding? = null
    private val binding get() = _binding!!
    private var clicked: Int = 0
    private var role: String? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private val TAG = "ChooseRoleFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseRoleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chooseRoleView()

        val btnContinue = binding.btnSubmit
        btnContinue.setOnClickListener {
            validateRole()
        }
    }

    private fun chooseRoleView() {
        clicked = 0
        val btnContinue = binding.btnSubmit
        val wrapRecruiter = binding.wrapRecruiter
        val wrapWorker = binding.wrapWorker

        wrapRecruiter.setOnClickListener {
            clicked = 1

            if (clicked == 0){
                wrapRecruiter.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                wrapWorker.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                role == null
            } else if (clicked == 1) {
                wrapRecruiter.setBackgroundResource(R.drawable.bg_choose_role_active)
                wrapWorker.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                role = "recruiter"
            } else if(clicked == 2) {
                wrapWorker.setBackgroundResource(R.drawable.bg_choose_role_active)
                wrapRecruiter.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                role = "worker"
            }
        }

        wrapWorker.setOnClickListener {
            clicked = 2

            if (clicked == 0){
                wrapRecruiter.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                wrapWorker.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                role = null
            } else if (clicked == 1) {
                wrapRecruiter.setBackgroundResource(R.drawable.bg_choose_role_active)
                wrapWorker.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                role = "recruiter"
            } else if(clicked == 2) {
                wrapWorker.setBackgroundResource(R.drawable.bg_choose_role_active)
                wrapRecruiter.setBackgroundResource(R.drawable.bg_choose_role_unactive)
                role = "worker"
            }
        }

    }

    private fun validateRole() {
        if (role.isNullOrEmpty()){
            textMessage("Pilih profil yang sesuai dengan anda")
        } else {
            val uid = SharedPreferences.getUid(requireContext())
            profileViewModel.updateRole(uid.toString(), role.toString())
            profileViewModel.updateRoleResponse.observe(viewLifecycleOwner){
                when(it) {
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        textMessage(it.data.toString())
                        goToChooseSkillsPage()
                    }
                    is BaseResponse.Error -> {
                        textMessage(it.msg.toString())
                    }
                    else -> {}
                }
            }
        }
    }

    private fun goToChooseSkillsPage(){
        if (findNavController().currentDestination?.id == R.id.choose_role_fragment){
            findNavController().navigate(ChooseRoleFragmentDirections.actionChooseRoleFragmentToChooseSkillsFragment())
        }
    }

    private fun goToHomePage() {
        if (findNavController().currentDestination?.id == R.id.choose_role_fragment){
            findNavController().navigate(ChooseRoleFragmentDirections.actionChooseRoleFragmentToNavigationHome())
        }
    }


    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}