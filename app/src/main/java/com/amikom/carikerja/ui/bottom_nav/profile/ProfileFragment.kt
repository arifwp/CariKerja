package com.amikom.carikerja.ui.bottom_nav.profile

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
    private var imageUrl: String? = null
    private var name: String? = null
    private var dob: String? = null
    private var address: String? = null
    private var TAG = "ProfileFragment"

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

        listener()

        val uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getProfile(uid.toString())
        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        imageUrl = it.data.imageUrl
                        name = it.data.name
                        dob = it.data.dob
                        address = it.data.address
                        binding.tvUserRole?.text = it.data.role
                        setProfile(it.data)
                        Log.d(TAG, "onViewCreated: ${it.data}")
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }

    }

    private fun listener() {

        val btnEdit = binding.wrapImageLogo
        btnEdit.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionNavigationProfileToBiodataFragment(
                imageUrl,
                name,
                dob,
                address
            ))
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
        if (data.address == null){
            binding.currentPosition.visibility = View.GONE
        } else if (data.address == "null"){
            binding.currentPosition.visibility = View.GONE
        } else if (data.address == ""){
            binding.currentPosition.visibility = View.GONE
        } else {
            binding.currentPosition.setText(data.address)
            binding.currentPosition.visibility = View.VISIBLE
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