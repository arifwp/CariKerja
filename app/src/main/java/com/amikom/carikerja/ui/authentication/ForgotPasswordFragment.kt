package com.amikom.carikerja.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentForgotPasswordBinding
import com.amikom.carikerja.databinding.FragmentLoginBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.viewmodels.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    private val TAG = "ForgotPasswordFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = binding.edEmail.text.toString().trim()


        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            if (email.isEmpty()){
                binding.edEmail.error = "Masukkan Email"
            } else {
                authenticationViewModel.forgotPassword(email)
                authenticationViewModel.forgotPasswordResponse.observe(viewLifecycleOwner){
                    it.getContentIfNotHandled().let {
                        when(it){
                            is BaseResponse.Loading -> {}
                            is BaseResponse.Success -> {
                                textMessage(it.data.toString())
                                goToLoginPage()
                            }
                            is BaseResponse.Error -> textMessage(it.msg.toString())
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    fun goToLoginPage(){
        if (findNavController().currentDestination?.id == R.id.forgot_password_fragment){
            findNavController().navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}