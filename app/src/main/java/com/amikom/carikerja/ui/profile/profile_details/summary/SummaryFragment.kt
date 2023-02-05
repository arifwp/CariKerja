package com.amikom.carikerja.ui.profile.profile_details.summary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.databinding.FragmentSummaryBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.UserProfile
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!
    private var TAG = "SummaryFragment"
    private var uid: String? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private val summaryViewModel: SummaryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()

        uid = SharedPreferences.getUid(requireContext())

        profileViewModel.getProfile(uid.toString())

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            validateInput()
        }

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateInput() {
        val summary = binding.edSummary.text.toString().trim()
        summaryViewModel.update(uid.toString(), summary)
    }

    private fun observe() {
        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            when(it) {
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    setProfile(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        summaryViewModel.updateSummary.observe(viewLifecycleOwner){
            when(it) {
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> textMessage(it.data.toString())
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun setProfile(data: UserProfile) {
        when {
            data.summary == "null" -> binding.edSummary.text = null
            data.summary != "null" -> binding.edSummary.setText(data.summary)
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}