package com.amikom.carikerja.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

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
                destination.id == R.id.add_certificate_fragment
            ) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
        navView.setupWithNavController(navController)
    }
}