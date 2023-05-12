package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.education

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.EducationAdapter
import com.amikom.carikerja.adapter.btnEducationEdit
import com.amikom.carikerja.databinding.FragmentEducationBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.EducationDetails
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EducationFragment : Fragment(), btnEducationEdit {

    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!
    private lateinit var educationAdapter: EducationAdapter
    private val educationViewModel: EducationViewModel by viewModels()
    private var TAG = "EducationFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateRv()
        val uid = SharedPreferences.getUid(requireContext())
        educationViewModel.getEducation(uid.toString())

        val btnAdd = binding.btnSubmit
        btnAdd.setOnClickListener {
            findNavController().navigate(
                EducationFragmentDirections.actionEducationFragmentToAddEducationFragment(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            ))
        }

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun btnOnClickEducation(data: EducationDetails) {
        findNavController().navigate(EducationFragmentDirections.actionEducationFragmentToAddEducationFragment(
            data.id,
            data.institution,
            data.degree,
            data.field_of_study,
            data.dateStart,
            data.dateEnd,
            data.description
        ))
    }

    private fun observe() {
        educationViewModel.getEducationResponse.observe(viewLifecycleOwner){ observe ->
            observe.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        educationAdapter.setEducationData(it.data)
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }
    }

    private fun initiateRv() {
        val recyclerViewEducation: RecyclerView = requireView().findViewById(R.id.rv_education)
        educationAdapter = EducationAdapter("EducationFragment", ArrayList())
        educationAdapter.listenerEducationEdit = this
        recyclerViewEducation.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = educationAdapter
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}