package com.amikom.carikerja.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentSplashScreenBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashScreenFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as AppCompatActivity?)!!.supportActionBar?.hide()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        lifecycleScope.launchWhenCreated {
            doCheck()
        }

    }

    private suspend fun doCheck() {
        val uid = SharedPreferences.getUid(requireContext())

        if (!uid.isNullOrEmpty()){
            checkHasRoleOrNot(uid)
        } else if (uid.isNullOrEmpty()){
            goToLoginPage()
        }
    }

    private suspend fun checkHasRoleOrNot(uid: String) {
        profileViewModel.checkRole(uid)
        profileViewModel.checkRoleResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    if (it.data.isNullOrEmpty()){
                        lifecycleScope.launch {
                            goToChooseRolePage()
                        }
                    } else if (it.data == "exist"){
                        lifecycleScope.launch{
                            goToHomePage()
                        }
                    }
                }
                is BaseResponse.Error -> {
                    lifecycleScope.launch{
                        goToLoginPage()
                    }
                }
            }
        }
    }

    private suspend fun goToChooseRolePage() {
        delay(2000)
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToChooseRoleFragment())
    }

    private suspend fun goToHomePage() {
        delay(2000)
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToNavigationHome())
    }

    private suspend fun goToLoginPage() {
        delay(2000)
        findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment())
    }

}