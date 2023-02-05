package com.amikom.carikerja.ui.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentHomeBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        checkProfileCompleteness()
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                //                R.id.notifications ->
                R.id.settings -> findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToSettingsFragment())
            }
            true
        }
    }

    private fun checkProfileCompleteness() {
        val uid = SharedPreferences.getUid(requireContext())
        profileViewModel.checkProfileCompleteness(uid.toString())
        profileViewModel.checkProfileCompletenessResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        showDialogProfileCompleteness()
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }
    }

    private fun showDialogProfileCompleteness() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_complete_profile)
        val btnRef = dialog.findViewById<Button>(R.id.btn_dialog_complete_profile)
        btnRef.setOnClickListener {
            dialog.dismiss()
            goToLoginPage()
        }

        dialog.setCancelable(true)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun goToLoginPage() {
        if (findNavController().currentDestination?.id == R.id.navigation_home){
            findNavController().navigate(R.id.action_navigation_home_to_navigation_profile)
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}