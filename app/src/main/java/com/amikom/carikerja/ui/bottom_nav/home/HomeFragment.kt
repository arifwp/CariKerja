package com.amikom.carikerja.ui.bottom_nav.home

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private var TAG = "HomeFragment"
    private var imageUrl: String? = null
    private var name: String? = null
    private var dob: String? = null
    private var address: String? = null
    private var uid: String? = null

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

        uid = SharedPreferences.getUid(requireContext())
        observe()
        listener()

    }

    private fun observe() {
        val uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getProfile(uid.toString())
        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let{
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        imageUrl = it.data.imageUrl
                        name = it.data.name
                        address = it.data.address
                        dob = it.data.dob
                        binding.tvUsername.setText(name)
                        Picasso.get()
                            .load("$imageUrl")
                            .error(R.drawable.dummy_avatar)
                            .into(binding.imgUser)

                        if (
                            it.data.imageUrl == "null" ||
                            it.data.address == "null" ||
                            it.data.dob == "null"
                        ) {
                            showDialogProfileCompleteness()
                        }
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }

        }
    }

    private fun listener() {
        val btnTransfer = binding.wrapTransfer
        btnTransfer.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToTransferFragment())
        }

        val btnTopUp = binding.wrapTopUp
        btnTopUp.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToTopupFragment())
        }

        val btnToSettings = binding.icSettings
        btnToSettings.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToSettingsFragment())
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
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToBiodataFragment(
                imageUrl,
                name,
                dob,
                address
            ))
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