package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.project

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.databinding.FragmentAddProjectBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.ProjectDetails
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddProjectFragment : Fragment() {

    private var _binding: FragmentAddProjectBinding? = null
    private val binding get() = _binding!!
    private val projectViewModel: ProjectViewModel by viewModels()
    private var TAG = "AddProjectFragment"
    private val args: AddProjectFragmentArgs by navArgs()
    private var uid: String? = null
    private var id: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddProjectBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateComponent()
        uid = SharedPreferences.getUid(requireContext())
        id = args.id

        if (id != null){
            observeDataEdit()
        } else {
            val btnSubmit = binding.btnSubmit
            btnSubmit.setOnClickListener {
                validateData(uid.toString())
            }
        }

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeDataEdit() {

        binding.deleteData.visibility = View.VISIBLE

        val projectName = binding.edProjectName
        val role = binding.edRole
        val dateStart = binding.edDateStart
        val dateEnd = binding.edDateEnd
        val desc = binding.edJobDescription

        projectName.setText(args.projectName)
        role.setText(args.role)
        dateStart.setText(args.dateStart)
        dateEnd.setText(args.dateEnd)
        desc.setText(args.description)

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            projectViewModel.editProject(
                uid.toString(),
                id.toString(),
                projectName.text.toString().trim(),
                role.text.toString().trim(),
                dateStart.text.toString().trim(),
                dateEnd.text.toString().trim(),
                desc.text.toString().trim()
            )
        }

        val btnDelete = binding.deleteData
        btnDelete.setOnClickListener {
            projectViewModel.deleteProject(uid.toString(), id.toString())
        }

    }

    private fun validateData(uid: String) {

        val projectName = binding.edProjectName.text.toString().trim()
        val role = binding.edRole.text.toString().trim()
        val dateStart = binding.edDateStart.text.toString().trim()
        val dateEnd = binding.edDateEnd.text.toString().trim()
        val description = binding.edJobDescription.text.toString().trim()

        val project = ProjectDetails(
            id = null,
            project_name = projectName,
            role = role,
            dateStart = dateStart,
            dateEnd = dateEnd,
            description = description
        )

        if (projectName.isEmpty()){
            binding.edProjectName.error = "Masukkan nama projek"
        }

        if (dateStart.isEmpty()){
            binding.edDateStart.error = "Masukkan keterangan waktu"
        }

        if (dateEnd.isEmpty()){
            binding.edDateEnd.error = "Masukkan keterangan waktu"
        }

        if (projectName.isNotEmpty() || dateStart.isNotEmpty() || dateEnd.isNotEmpty()){
            projectViewModel.addProject(uid, project)
        }

    }

    private fun initiateComponent() {
        binding.edDateStart.setOnClickListener {

            var c = Calendar.getInstance()
            var cDay = c.get(Calendar.DAY_OF_MONTH)
            var cMonth = c.get(Calendar.MONTH)
            var cYear = c.get(Calendar.YEAR)

            val calenderDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    cDay = dayOfMonth
                    cMonth = month
                    cYear = year
                    val dateStart = binding.edDateStart
                    dateStart.setText("$cDay-${cMonth+1}-$cYear")

                }, cYear, cMonth, cDay)
            calenderDialog.show()
        }

        binding.edDateEnd.setOnClickListener {

            var c = Calendar.getInstance()
            var cDay = c.get(Calendar.DAY_OF_MONTH)
            var cMonth = c.get(Calendar.MONTH)
            var cYear = c.get(Calendar.YEAR)

            val calenderDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    cDay = dayOfMonth
                    cMonth = month
                    cYear = year
                    val dateEnd = binding.edDateEnd
                    dateEnd.setText("$cDay-${cMonth+1}-$cYear")

                }, cYear, cMonth, cDay)
            calenderDialog.show()
        }
    }

    private fun observe() {
        projectViewModel.addProjectResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> textMessage(it.data)
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        projectViewModel.editProjectResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data.toString())
                    findNavController().popBackStack()
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        projectViewModel.deleteProjectResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data.toString())
                    findNavController().popBackStack()
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}