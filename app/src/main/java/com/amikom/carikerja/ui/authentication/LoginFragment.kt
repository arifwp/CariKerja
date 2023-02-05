package com.amikom.carikerja.ui.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentLoginBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.AuthenticationViewModel
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel : AuthenticationViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val TAG = "LoginFragmentLog"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogin = binding.btnLogin
        btnLogin.setOnClickListener {
            validateLogin()
        }

        val btnToRegister = binding.tvSignUp
        btnToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_fragment_to_register_fragment)
        }

        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                binding.tilPassword.isEndIconVisible = true
                binding.edPassword.error = null
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })
    }

    private fun validateLogin() {
        binding.apply {
            val email = edEmail.text.toString().trim()
            val password = edPassword.text.toString()
            if (email.isEmpty()){
                edEmail.error = "Masukkan email anda"
            }
            if (password.isEmpty()){
                tilPassword.isEndIconVisible = false
                edPassword.error = "Masukkan password anda"
            }
            if (email.isNotEmpty() && password.isNotEmpty()){
                authenticationViewModel.login(email, password)
                authenticationViewModel.signInStatus.observe(viewLifecycleOwner){
                    it.getContentIfNotHandled().let {
                        when(it){
                            is BaseResponse.Loading -> {}
                            is BaseResponse.Success -> {
                                SharedPreferences.saveUid(requireContext(), it.data)
                                checkHasRoleOrNot(it.data)
                            }
                            is BaseResponse.Error -> textMessage(it.msg.toString())
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun checkHasRoleOrNot(uid: String) {
        profileViewModel.checkRole(uid)
        profileViewModel.checkRoleResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage("Berhasil masuk")
                    if (it.data.isNullOrEmpty()){
                        goToChooseRolePage()
                    } else if (it.data == "exist"){
                        goToHomePage()
                    }
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun goToChooseRolePage() {
        if (findNavController().currentDestination?.id == R.id.login_fragment){
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToChooseRoleFragment())
        }
    }


    private fun goToHomePage() {
        if (findNavController().currentDestination?.id == R.id.login_fragment){
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToNavigationHome())
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}