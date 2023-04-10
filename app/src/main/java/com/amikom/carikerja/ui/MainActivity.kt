package com.amikom.carikerja.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.ActivityMainBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val profileViewModel: ProfileViewModel by viewModels()
    private var role: String? = null
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = SharedPreferences.getUid(this)

        val navView: BottomNavigationView = binding.navView

        val userLogout = intent.getStringExtra("USER_LOGOUT")
        Log.d(TAG, "onCreateUSER_LOGOUT: $userLogout")
        if (userLogout != null){
            if(android.os.Build.VERSION.SDK_INT >= 21)
            {
                finishAndRemoveTask();
            }
            else
            {
                finish();
            }
        }

        val userRole = SharedPreferences.getRole(this)
        Log.d(TAG, "onCreateRoleMain: $userRole")

        if (userRole != null){
            profileViewModel.getRole(uid.toString())
            profileViewModel.getRoleResponse.observe(this){
                it?.getContentIfNotHandled().let {
                    when(it){
                        is BaseResponse.Loading -> {}
                        is BaseResponse.Success -> {
                            val userRole = it.data
                            Log.d(TAG, "onCreateRole: $userRole")
                            when{
                                userRole.toString() == "recruiter" -> {
                                    navView.menu.removeItem(R.id.navigation_history_work)
                                }
                                userRole.toString() == "worker" -> {
                                    navView.menu.removeItem(R.id.navigation_history_post_job_work)
                                }
                            }
                        }
                        is BaseResponse.Error -> {}
                        else -> {}
                    }
                }
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.splash_screen_fragment) {
                binding.container.setPadding(0, 0, 0, 0)
                navView.visibility = View.GONE
            } else if(
                destination.id == R.id.login_fragment ||
                destination.id == R.id.register_fragment ||
                destination.id == R.id.biodata_fragment ||
                destination.id == R.id.settings_fragment ||
                destination.id == R.id.contact_information_fragment ||
                destination.id == R.id.summary_fragment ||
                destination.id == R.id.work_experience_fragment ||
                destination.id == R.id.add_work_experience_fragment ||
                destination.id == R.id.education_fragment ||
                destination.id == R.id.add_education_fragment ||
                destination.id == R.id.project_fragment ||
                destination.id == R.id.add_project_fragment ||
                destination.id == R.id.certificate_fragment ||
                destination.id == R.id.add_certificate_fragment ||
                destination.id == R.id.topup_fragment ||
                destination.id == R.id.transfer_fragment ||
                destination.id == R.id.choose_role_fragment ||
                destination.id == R.id.choose_skills_fragment ||
                destination.id == R.id.bank_account_fragment ||
                destination.id == R.id.list_skill_profile_fragment ||
                destination.id == R.id.list_applicant_fragment ||
                destination.id == R.id.detail_job_fragment ||
                destination.id == R.id.add_list_skill_profile_fragment  ||
                destination.id == R.id.add_post_job_fragment ||
                destination.id == R.id.update_password_fragment
            ) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
        navView.setupWithNavController(navController)
    }

}