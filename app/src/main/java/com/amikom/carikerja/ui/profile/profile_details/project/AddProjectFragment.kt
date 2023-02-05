package com.amikom.carikerja.ui.profile.profile_details.project

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
        val uid = SharedPreferences.getUid(requireContext())

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            validateData(uid.toString())
        }

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateData(uid: String) {

        val projectName = binding.edProjectName.text.toString().trim()
        val role = binding.edRole.text.toString().trim()
        val dateStart = binding.edDateStart.text.toString().trim()
        val dateEnd = binding.edDateEnd.text.toString().trim()
        val description = binding.edJobDescription.text.toString().trim()

        val project = ProjectDetails(
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
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}