package com.amikom.carikerja.ui.bottom_nav.work.post_job

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
import com.amikom.carikerja.databinding.FragmentAddPostJobBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.JobDetails
import com.amikom.carikerja.models.PublishedJob
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddPostJobFragment : Fragment() {

    private var _binding: FragmentAddPostJobBinding? = null
    private val binding get() = _binding!!
    private val TAG = "AddPostJobFragment"
    private val jobViewModel: JobViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val args: AddPostJobFragmentArgs by navArgs()
    private var uid: String? = null
    private var countDays: String? = null
    private var currentDateTime: String? = null
    private var publishedJob: PublishedJob? = null
//    private var listSkills: ArrayList<String>? = null
    private var listSkills: List<String?> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostJobBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getSkill(uid.toString())
        observe()
        initiateInputComponent()

        val icBack = binding.icBack
        icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        publishedJob = PublishedJob(
            id_job = null
        )

        if (args.id.isNullOrEmpty()){
            val btnSubmit = binding.btnSubmit
            btnSubmit.setOnClickListener {
                validateData()
            }
        } else {
            with(binding){
                edJobTitle.setText(args.jobTitle)
                edSalary.setText(args.salary)
                edDateStart.setText(args.dateStart)
                edDateEnd.setText(args.dateEnd)
                edLocation.setText(args.jobAddress)
                edJobDescription.setText(args.jobDescription)
            }

            val btnSubmit = binding.btnSubmit
            btnSubmit.setOnClickListener {
                populateEditData()
            }
        }

    }

    private fun populateEditData() {

        val jobTitle = binding.edJobTitle.text.toString().trim()
        val employeeType = binding.edEmployeeType.selectedItem.toString().trim()
        val salary = binding.edSalary.text.toString().trim()
        val dateStart = binding.edDateStart.text.toString().trim()
        val dateEnd = binding.edDateEnd.text.toString().trim()
        val jobAdress = binding.edLocation.text.toString().trim()
        val jobDescription = binding.edJobDescription.text.toString().trim()
        val jobCategory = binding.edJobCategory.selectedItem.toString().trim()

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val currentDate = sdf.format(Date())
        currentDateTime = currentDate

        //HH converts hour in 24 hours format (0-23), day calculation
        val format = SimpleDateFormat("dd-MM-yyyy")
        var d1: Date? = null
        var d2: Date? = null

        try {
            d1 = format.parse(dateStart)
            d2 = format.parse(dateEnd)

            val diff = d2.time - d1.time
            val diffDays = diff / (24 * 60 * 60 * 1000)
            countDays = diffDays.toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val jobUpdate = JobDetails(
            id = args.id,
            uid = args.uid,
            job_title = jobTitle,
            person_who_post = args.personWhoPost,
            image_url = args.imageUrl,
            dateStart = dateStart,
            dateEnd = dateEnd,
            total_day = countDays,
            job_address = jobAdress,
            job_description = jobDescription,
            employee_type = employeeType,
            job_category = jobCategory,
            salary = salary,
            post_time = args.postTime,
            edited_at = currentDateTime,
            job_status = args.jobStatus
        )

        when {
            jobTitle.isEmpty() -> binding.edJobTitle.error = "Masukkan judul pekerjaan"
            dateStart.isEmpty() -> binding.edDateStart.error = "Masukkan tanggal dimulainya pekerjaan"
            dateEnd.isEmpty() -> binding.edDateEnd.error = "Masukkan tanggal berakhirnya pekerjaan"
            jobAdress.isEmpty() -> binding.edLocation.error = "Masukkan alamat lokasi pekerjaan"
            else -> jobViewModel.editJob(uid.toString(), jobUpdate)
        }

    }

    private fun validateData() {
        val jobTitle = binding.edJobTitle.text.toString().trim()
        val employeeType = binding.edEmployeeType.selectedItem.toString().trim()
        val salary = binding.edSalary.text.toString().trim()
        val dateStart = binding.edDateStart.text.toString().trim()
        val dateEnd = binding.edDateEnd.text.toString().trim()
        val jobAdress = binding.edLocation.text.toString().trim()
        val jobDescription = binding.edJobDescription.text.toString().trim()
        val jobCategory = binding.edJobCategory.selectedItem.toString().trim()

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val currentDate = sdf.format(Date())
        currentDateTime = currentDate


        //HH converts hour in 24 hours format (0-23), day calculation
        val format = SimpleDateFormat("dd-MM-yyyy")
        var d1: Date? = null
        var d2: Date? = null

        try {
            d1 = format.parse(dateStart)
            d2 = format.parse(dateEnd)

            val diff = d2.time - d1.time
            val diffDays = diff / (24 * 60 * 60 * 1000)
            countDays = diffDays.toString()

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val job = JobDetails(
            id = null,
            uid = null,
            job_title = jobTitle,
            person_who_post = null,
            image_url = null,
            dateStart = dateStart,
            dateEnd = dateEnd,
            total_day = countDays,
            job_address = jobAdress,
            job_description = jobDescription,
            employee_type = employeeType,
            job_category = jobCategory,
            salary = salary,
            post_time = currentDateTime,
            edited_at = null,
            job_status = null
        )

        when {
            jobTitle.isEmpty() -> binding.edJobTitle.error = "Masukkan judul pekerjaan"
            dateStart.isEmpty() -> binding.edDateStart.error = "Masukkan tanggal dimulainya pekerjaan"
            dateEnd.isEmpty() -> binding.edDateEnd.error = "Masukkan tanggal berakhirnya pekerjaan"
            jobAdress.isEmpty() -> binding.edLocation.error = "Masukkan alamat lokasi pekerjaan"
            else -> jobViewModel.addJob(uid.toString(), job)
        }

    }

    private fun observe() {
        jobViewModel.addJobResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        textMessage(it.data.toString())
                        findNavController().popBackStack()
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }

        jobViewModel.editJobResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        textMessage(it.data)
                        findNavController().navigate(AddPostJobFragmentDirections.actionAddPostJobFragmentToNavigationWork())
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
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

        if (args.employeeType.isNullOrEmpty()){
            // Initiate spinner
            val employeeTypeDropdown = resources.getStringArray(R.array.employee_type)
            val employeeTypeDropdownAdapter = ArrayAdapter(requireContext(),R.layout.layout_dropdown_item,employeeTypeDropdown)
            val spinnerEmployeeType = binding.edEmployeeType
            spinnerEmployeeType.adapter = employeeTypeDropdownAdapter
        } else {
            val employeeTypeDropdown = resources.getStringArray(R.array.employee_type)
            val employeeTypeDropdownAdapter = ArrayAdapter(requireContext(),R.layout.layout_dropdown_item,employeeTypeDropdown)
            val spinnerEmployeeType = binding.edEmployeeType
            spinnerEmployeeType.adapter = employeeTypeDropdownAdapter
            val selection = args.employeeType
            val spinnerPosition: Int = employeeTypeDropdownAdapter.getPosition(selection)
            spinnerEmployeeType.setSelection(spinnerPosition)
        }



        profileViewModel.getSkillResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        if (it.data != null){
                            val response = it.data.map { it.name }

                            if (args.jobCategory.isNullOrEmpty()){
                                val jobCategoryDropdown = response
                                val jobCategoryDropdownAdapter = ArrayAdapter(requireContext(), R.layout.layout_dropdown_item, jobCategoryDropdown)
                                val spinnerJobCategory = binding.edJobCategory
                                spinnerJobCategory.adapter = jobCategoryDropdownAdapter
                            } else {
                                val jobCategoryDropdown = response
                                val jobCategoryDropdownAdapter = ArrayAdapter(requireContext(), R.layout.layout_dropdown_item, jobCategoryDropdown)
                                val spinnerJobCategory = binding.edJobCategory
                                spinnerJobCategory.adapter = jobCategoryDropdownAdapter
                                val selection = args.jobCategory
                                val spinnerPosition: Int = jobCategoryDropdownAdapter.getPosition(selection)
                                spinnerJobCategory.setSelection(spinnerPosition)
                            }

                        } else {
                            textMessage("Cocokkan data kategori pekerjaan")
                        }
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }



    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}