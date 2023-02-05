package com.amikom.carikerja.ui.profile.profile_details.contact_information

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.databinding.FragmentContactInformationBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.UserProfile
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactInformationFragment : Fragment() {

    private var _binding: FragmentContactInformationBinding? = null
    private val binding get() = _binding!!
    private var TAG = "ContactInformationFragment"
    private val profileViewModel: ProfileViewModel by viewModels()
    private val contactInformationViewModel: ContactInformationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentContactInformationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getProfile(uid.toString())


        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            val phone = binding.edPhone.text.toString().trim()
            val email = binding.edEmail.text.toString().trim()
            val address = binding.edLocation.text.toString().trim()
            contactInformationViewModel.update(uid.toString(), phone, email, address)
        }
    }

    private fun observe() {

        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    setProfile(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        contactInformationViewModel.updateResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data.toString())
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }


    }

    private fun setProfile(data: UserProfile) {
        binding.edPhone.setText(data.phone)
        binding.edEmail.setText(data.email)
        when {
            data.address == "null" -> binding.edLocation.text = null
            data.address != "null" -> binding.edLocation.setText(data.address)
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}