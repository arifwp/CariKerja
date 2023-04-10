package com.amikom.carikerja.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentSettingsBinding
import com.amikom.carikerja.databinding.FragmentUpdatePasswordBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.AuthenticationViewModel
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())
        observe()

        val icBack = binding.icBack
        icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            validateData()
        }

    }

    private fun observe() {
        authenticationViewModel.changePasswordResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
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
    }

    private fun validateData() {
        val oldPassword = binding.edOldPassword.text.toString().trim()
        val newPassword = binding.edNewPassword.text.toString().trim()
        val confirmNewPassword = binding.edConfirmNewPassword.text.toString().trim()

        when {
            oldPassword.isNullOrEmpty() -> {
                binding.edOldPassword.error = "Masukkan kata sandi yang lama"
            }
            newPassword.isNullOrEmpty() -> {
                binding.edNewPassword.error = "Masukkan kata sandi baru"
            }
            confirmNewPassword.isNullOrEmpty() -> {
                binding.edConfirmNewPassword.error = "Masukkan konfirmasi kata sandi baru"
            }
            newPassword != confirmNewPassword -> {
                binding.edNewPassword.error = "Kata sandi harus sama"
                binding.edConfirmNewPassword.error = "Kata sandi harus sama"
            }
            else -> {
                authenticationViewModel.changePassword(uid.toString(), oldPassword, newPassword)
            }
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}