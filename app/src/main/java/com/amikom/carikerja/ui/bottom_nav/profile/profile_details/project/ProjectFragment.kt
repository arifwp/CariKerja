package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.project

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
import com.amikom.carikerja.adapter.ProjectAdapter
import com.amikom.carikerja.adapter.btnEditProjectListener
import com.amikom.carikerja.databinding.FragmentProjectBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.ProjectDetails
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectFragment : Fragment(), btnEditProjectListener {

    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!
    private val projectViewModel: ProjectViewModel by viewModels()
    private lateinit var projectAdapter: ProjectAdapter
    private var TAG = "EducationFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProjectBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateRv()
        val uid = SharedPreferences.getUid(requireContext())
        projectViewModel.getProject(uid.toString())

        val btnAddProject = binding.btnSubmit
        btnAddProject.setOnClickListener {
            findNavController().navigate(
                ProjectFragmentDirections.actionProjectFragmentToAddProjectFragment(
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

    override fun btnOnClickEdit(data: ProjectDetails) {
        findNavController().navigate(ProjectFragmentDirections.actionProjectFragmentToAddProjectFragment(
            data.id,
            data.project_name,
            data.role,
            data.dateStart,
            data.dateEnd,
            data.description
        ))
    }

    private fun initiateRv(){
        val recyclerViewProject: RecyclerView = requireView().findViewById(R.id.rv_project)
        projectAdapter = ProjectAdapter("ProjectFragment", ArrayList())
        projectAdapter.listenerEditProject = this
        recyclerViewProject.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = projectAdapter
        }
    }

    private fun observe() {
        projectViewModel.getProjectResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    projectAdapter.setProjectData(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())

            }
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}