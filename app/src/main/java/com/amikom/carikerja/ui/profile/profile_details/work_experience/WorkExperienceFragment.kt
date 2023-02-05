package com.amikom.carikerja.ui.profile.profile_details.work_experience

import android.os.Bundle
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
import com.amikom.carikerja.adapter.WorkExperienceAdapter
import com.amikom.carikerja.databinding.FragmentWorkExperienceBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkExperienceFragment : Fragment() {

    private var _binding: FragmentWorkExperienceBinding? = null
    private val binding get() = _binding!!
    private var TAG = "WorkExperienceFragment"
    private var uid: String? = null
    private val workExperienceViewModel: WorkExperienceViewModel by viewModels()
    private lateinit var workExperienceAdapter: WorkExperienceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWorkExperienceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateRv()

        uid = SharedPreferences.getUid(requireContext())
        workExperienceViewModel.getWorkExp(uid.toString())

        val btnAdd = binding.btnAddWorkExperience
        btnAdd.setOnClickListener {
            findNavController().navigate(WorkExperienceFragmentDirections.actionWorkExperienceFragmentToAddWorkExperienceFragment())
        }

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observe() {
        workExperienceViewModel.getWorkExp.observe(viewLifecycleOwner){ observe ->
            observe.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        workExperienceAdapter.setWorkExpData(it.data)
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }
    }

    private fun initiateRv(){
        val recyclerViewWorkExp: RecyclerView = requireView().findViewById(R.id.rv_work_experience)
        workExperienceAdapter = WorkExperienceAdapter(ArrayList())
        recyclerViewWorkExp.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = workExperienceAdapter
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}