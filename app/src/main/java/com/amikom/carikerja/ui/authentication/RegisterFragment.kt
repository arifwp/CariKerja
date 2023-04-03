package com.amikom.carikerja.ui.authentication

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentRegisterBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.AllEvents
import com.amikom.carikerja.viewmodels.AuthenticationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    private lateinit var databaseReference : DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnRegister = binding.btnRegister
        btnRegister.setOnClickListener {
            validateRegister()
        }
        databaseReference = Firebase.database.reference
        auth = Firebase.auth

        val btnToLogin = binding.tvSignIn
        btnToLogin.setOnClickListener {
            goToLogin()
        }

        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                binding.tilPassword.isEndIconVisible = true
                binding.edPassword.error = null
            }

            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })

        binding.edConfirmPassword.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tilConfirmPassword.isEndIconVisible = true
                binding.edConfirmPassword.error = null
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

    }

    private fun validateRegister() {
        val name = binding.edFullName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phone = binding.edPhone.text.toString().trim()
        val password = binding.edPassword.text.toString()
        val confirmPassword = binding.edConfirmPassword.text.toString()

        when {
            name.isEmpty() -> binding.edFullName.error = "Masukkan nama lengkap"
            email.isEmpty() -> binding.edEmail.error = "Masukkan email"
            phone.isEmpty() -> binding.edPhone.error = "Masukkan nomor whatsapp"
            password.isEmpty() -> binding.edPassword.error = "Masukkan kata sandi"
//            password.length <= 6 -> binding.edPassword.error = "Kata sandi harus lebih dari 7 karakter"
            confirmPassword.isEmpty() -> binding.edConfirmPassword.error = "Masukkan konfirmasi kata sandi"
            password != confirmPassword -> {
                binding.edPassword.error = "Kata sandi tidak sama"
                binding.edConfirmPassword.error = "Kata sandi tidak sama"
            }
            else -> {
                authenticationViewModel.userDetails.name = name
                authenticationViewModel.userDetails.email = email
                authenticationViewModel.userDetails.phone = phone
                authenticationViewModel.userDetails.password = password
                authenticationViewModel.register(email, password)
                authenticationViewModel.registerResponse.observe(viewLifecycleOwner){
                    it.getContentIfNotHandled().let {
                        when(it){
                            is BaseResponse.Loading -> {}
                            is BaseResponse.Success -> {
                                goToLogin()
                                textMessage(it.data)
                            }
                            is BaseResponse.Error -> textMessage(it.msg.toString())
                            else -> {}
                        }
                    }
                }
            }
        }

//        if (name.isEmpty()){
//            binding.edFullName.error = "Masukkan nama lengkap"
//        }
//        if (email.isEmpty()){
//            binding.edEmail.error = "Masukkan email"
//        }
//        if (phone.isEmpty()){
//            binding.edPhone.error = "Masukkan nomor whatsapp"
//        }
//        if (password.isEmpty()){
//            binding.edPassword.error = "Masukkan kata sandi"
//        }
//        if (password.length <= 6)
//        if (confirmPassword.isEmpty()){
//            binding.edConfirmPassword.error = "Masukkan konfirmasi kata sandi"
//        }
//        if (password != confirmPassword){
//            binding.edPassword.error = "Kata sandi tidak sama"
//            binding.edConfirmPassword.error = "Kata sandi tidak sama"
//        }
//        if (
//            name.isNotEmpty() ||
//            email.isNotEmpty() ||
//            phone.isNotEmpty() ||
//            password.isNotEmpty() ||
//            confirmPassword.isNotEmpty() ||
//            password.length >= 6 ||
//            password == confirmPassword
//        ) {
//            authenticationViewModel.userDetails.name = name
//            authenticationViewModel.userDetails.email = email
//            authenticationViewModel.userDetails.phone = phone
//            authenticationViewModel.userDetails.password = password
//            authenticationViewModel.register(email, password)
//            authenticationViewModel.registerResponse.observe(viewLifecycleOwner){
//                it.getContentIfNotHandled().let {
//                    when(it){
//                        is BaseResponse.Loading -> {}
//                        is BaseResponse.Success -> {
//                            goToLogin()
//                            textMessage(it.data)
//                        }
//                        is BaseResponse.Error -> textMessage(it.msg.toString())
//                        else -> {}
//                    }
//                }
//            }
//        }

//        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
//            if (it.isSuccessful) {
//                textMessage("Berhasil mendaftarkan akun")
//                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
//            } else {
//                try {
//                    throw it.exception!!
//                } catch (e: FirebaseAuthUserCollisionException) {
//                    // email already in use
//                    textMessage("Email sudah terpakai")
//                }
//                textMessage("Gagal membuat akun, coba lagi nanti")
//            }
//        }

    }

    private fun goToLogin() {
        if (findNavController().currentDestination?.id == R.id.register_fragment){
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }


    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}