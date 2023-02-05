package com.amikom.carikerja.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentProfileBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.UserProfile
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private var TAG = "ProfileFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        listener()

        val uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getProfile(uid.toString())
        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    setProfile(it.data)
//                    Log.d(TAG, "onViewCreated: ${it.data}")
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

    }

    private fun listener() {

        val btnEdit = binding.btnEdit
        btnEdit.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToBiodataFragment())
        }

        val contactInformation = binding.wrapContactInformation
        contactInformation.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToContactInformationFragment())
        }

        val summary = binding.wrapSummary
        summary.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToSummaryFragment())
        }

        val workExp = binding.wrapWorkExp
        workExp.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToWorkExperienceFragment())
        }

        val education = binding.wrapEducation
        education.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToEducationFragment())
        }

        val project = binding.wrapProject
        project.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToProjectFragment())
        }

        val certificate = binding.wrapCertification
        certificate.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionNavigationProfileToCertificateFragment())
        }
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        toolbar?.inflateMenu(R.menu.main_menu)
        toolbar?.setOnMenuItemClickListener {
            when(it.itemId){
                //                R.id.notifications ->
                R.id.settings -> findNavController().navigate(R.id.action_navigation_profile_to_settings_fragment)
            }
            true
        }
    }

    private fun setProfile(data: UserProfile) {
        if (data.imageUrl == ""){
            binding.userImage.setBackgroundResource(R.drawable.dummy_avatar)
        } else {
            Picasso.get()
                .load(data.imageUrl)
                .error(R.drawable.dummy_avatar)
                .into(binding.userImage)
        }
        binding.userName.setText(data.name)
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}