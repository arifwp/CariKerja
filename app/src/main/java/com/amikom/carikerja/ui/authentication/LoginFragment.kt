package com.amikom.carikerja.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentLoginBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.ui.MainActivity
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.AuthenticationViewModel
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel : AuthenticationViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val TAG = "LoginFragment"

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

        val btnForgotPassword = binding.tvForgotPassword
        btnForgotPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
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

            when {
                email.isEmpty() -> edEmail.error = "Masukkan email anda"
                password.isEmpty() -> {
                    tilPassword.isEndIconVisible = false
                    edPassword.error = "Masukkan password anda"
                }
                else -> {
                    authenticationViewModel.login(email, password)
                    authenticationViewModel.signInStatus.observe(viewLifecycleOwner){
                        it.getContentIfNotHandled().let {
                            when(it){
                                is BaseResponse.Loading -> {}
                                is BaseResponse.Success -> {
                                    SharedPreferences.saveUid(requireContext(), it.data)
                                    saveToken(it.data)
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
    }

    private fun saveToken(uid: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toas
//            val msg = resources.getString(R.string.msg_token_fmt, token)

            authenticationViewModel.saveNewToken(uid.toString(), token.toString())
            Log.d(TAG, token)
        })
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
                    } else if (it.data.isNotEmpty()){
                        Log.d(TAG, "checkHasRoleOrNot: ${it.data}")
                        val userRole = it.data
                        val navView = requireActivity().findViewById<BottomNavigationView>(com.amikom.carikerja.R.id.nav_view)
                        when{
                            userRole.toString() == "recruiter" -> {
                                navView.menu.removeItem(com.amikom.carikerja.R.id.navigation_history_work)
                            }
                            userRole.toString() == "worker" -> {
                                navView.menu.removeItem(com.amikom.carikerja.R.id.navigation_history_post_job_work)
                            }
                        }
                        SharedPreferences.saveRole(requireContext(), it.data.toString())
                        checkHasSkillsOrNot(uid.toString())
                    }
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
                else -> {}
            }
        }
    }

    private fun checkHasSkillsOrNot(uid: String){
        profileViewModel.checkSkills(uid)
        profileViewModel.checkSkillsResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    if (it.data == false){
                        goToChooseSkillsPage()

                    } else if (it.data == true){
//                        restartApp()
                        goToHomePage()
                    }
                }
                is BaseResponse.Error -> {
                    textMessage(it.msg.toString())
                }
                else -> {}
            }
        }
    }

    private fun restartApp(){

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

//        val ctx: Context = getApplicationContext()
//        val pm: PackageManager = ctx.packageManager
//        val intent = pm.getLaunchIntentForPackage(ctx.packageName)
//        val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
//        ctx.startActivity(mainIntent)
//        Runtime.getRuntime().exit(0)
    }

    private fun goToChooseSkillsPage(){
        if (findNavController().currentDestination?.id == R.id.login_fragment){
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToChooseSkillsFragment())
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