package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.work_experience

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentAddWorkExperienceBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.Exp
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddWorkExperienceFragment : Fragment() {

    private var _binding: FragmentAddWorkExperienceBinding? = null
    private val binding get() = _binding!!
    private val workExperienceViewModel: WorkExperienceViewModel by viewModels()
    private var TAG = "AddWorkExperienceFragment"
    private val args: AddWorkExperienceFragmentArgs by navArgs()
    private var uid: String? = null
    private var id: String? = null
    private var employee_type: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddWorkExperienceBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateInputComponent()

        uid = SharedPreferences.getUid(requireContext())


        val btnSubmit = binding.btnSubmit
        id = args.id
        if (id != null){
            observeDataEdit()
        } else {
            btnSubmit.setOnClickListener {
                validateData()
            }
        }

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeDataEdit() {
        binding.deleteData.visibility = View.VISIBLE
        employee_type = args.employeeType

        val jobTitle = binding.edJobTitle
        val company = binding.edCompany
        val dateStart = binding.edDateStart
        val dateEnd = binding.edDateEnd
        val jobDescription = binding.edJobDescription
        val employeeType = binding.edEmployeeType
        val jobAddress = binding.edLocation

        jobTitle.setText(args.jobTiitle)
        company.setText(args.company)
        dateStart.setText(args.dateStart)
        dateEnd.setText(args.dateEnd)
        jobDescription.setText(args.jobDescription)
        jobAddress.setText(args.jobAddress)

        val exp = Exp(
            id = args.id,
            job_title = binding.edJobTitle.text.toString().trim(),
            company = binding.edCompany.text.toString().trim(),
            dateStart = binding.edDateStart.text.toString().trim(),
            dateEnd = binding.edDateEnd.text.toString().trim(),
            job_description = binding.edJobDescription.text.toString().trim(),
            employee_type = binding.edEmployeeType.selectedItem.toString(),
            job_address = binding.edLocation.text.toString().trim()
        )



        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            workExperienceViewModel.editWorkExp(
                uid.toString(),
                id,
                jobTitle.text.toString().trim(),
                company.text.toString().trim(),
                dateStart.text.toString().trim(),
                dateEnd.text.toString().trim(),
                jobDescription.text.toString().trim(),
                employeeType.selectedItem.toString(),
                jobAddress.text.toString().trim()
            )
            Log.d(TAG, "observeDataEdit: ${binding.edCompany.text.toString().trim()}")
        }

        val btnDelete = binding.deleteData
        btnDelete.setOnClickListener {
            workExperienceViewModel.deleteWorkExp(uid.toString(), args.id.toString())
        }

    }

    private fun validateData() {
        val jobTitle = binding.edJobTitle.text.toString().trim()
        val company = binding.edCompany.text.toString().trim()
        val jobDesc = binding.edJobDescription.text.toString().trim()
        val jobAddress = binding.edLocation.text.toString().trim()
        val employeeType = binding.edEmployeeType.selectedItem.toString()
        val dateStart = binding.edDateStart.text.toString()
        val dateEnd = binding.edDateEnd.text.toString()

        val workExperience = Exp(
            id = null,
            job_title = jobTitle,
            company = company,
            dateStart = dateStart,
            dateEnd = dateEnd,
            job_description = jobDesc,
            employee_type = employeeType,
            job_address = jobAddress,
        )

        workExperienceViewModel.addWorkExperience(uid.toString(), workExperience)
    }

    private fun observe() {
        workExperienceViewModel.addWorkExperienceResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> textMessage(it.data.toString())
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        workExperienceViewModel.editWorkExp.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        workExperienceViewModel.deleteWorkExpResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> textMessage(it.data.toString())
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun initiateInputComponent(){
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

        // Initiate spinner
        val employeeTypeDropdown = resources.getStringArray(R.array.employee_type)
        val employeeTypeDropdownAdapter = ArrayAdapter(requireContext(),R.layout.layout_dropdown_item,employeeTypeDropdown)
        val spinnerEmployeeType = binding.edEmployeeType
        spinnerEmployeeType.adapter = employeeTypeDropdownAdapter
        if (args.employeeType != null){
            val selection = args.employeeType
            val spinnerPosition: Int = employeeTypeDropdownAdapter.getPosition(selection)
            spinnerEmployeeType.setSelection(spinnerPosition)
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}