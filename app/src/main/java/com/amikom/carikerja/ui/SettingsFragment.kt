package com.amikom.carikerja.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentSettingsBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.AuthenticationViewModel
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.jakewharton.processphoenix.ProcessPhoenix
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val authenticationViewModel: AuthenticationViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logout = binding.wrapLogout
        logout.setOnClickListener {
            doLogout()
        }
    }

    private fun doLogout() {
        authenticationViewModel.logout()
        authenticationViewModel.logoutResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        SharedPreferences.clearData(requireContext())
                        textMessage(it.data.toString())
                        goToMainActivity()
//                        goToLoginPage()
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }
    }

    private fun goToMainActivity(){

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("USER_LOGOUT", "1")
        startActivity(intent)
//        ProcessPhoenix.triggerRebirth(context, intent)
    }

    private fun goToLoginPage() {
        if (findNavController().currentDestination?.id == R.id.settings_fragment){
            findNavController().navigate(R.id.action_settings_fragment_to_login_fragment)
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}