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
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentAddPostJobBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.JobDetails
import com.amikom.carikerja.models.PublishedJob
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddPostJobFragment : Fragment() {

    private var _binding: FragmentAddPostJobBinding? = null
    private val binding get() = _binding!!
    private val TAG = "AddPostJobFragment"
    private val jobViewModel: JobViewModel by viewModels()
    private var uid: String? = null
    private var countDays: String? = null
    private var currentDateTime: String? = null
    private var publishedJob: PublishedJob? = null

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
        observe()
        initiateInputComponent()

        publishedJob = PublishedJob(
            id_job = null
        )

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            validateData()
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
            post_time = currentDateTime
        )

//        if (jobTitle.isEmpty()){
//            binding.edJobTitle.error = "Masukkan judul pekerjaan yang ingin anda cari"
//        }
//
//        if (dateStart.isEmpty()){
//            binding.edDateStart.error = "Masukkan tanggal dimulainya pekerjaan"
//        }
//
//        if (dateEnd.isEmpty()){
//            binding.edDateEnd.error = "Masukkan tanggal berakhirnya pekerjaan"
//        }
//
//        if (jobTitle.isNotEmpty() || dateStart.isNotEmpty() || dateEnd.isNotEmpty()){
//            jobViewModel.addJob(uid.toString(), job, publishedJob)
//        }

        when {
            jobTitle.isEmpty() -> binding.edJobTitle.error = "Masukkan judul pekerjaan"
            dateStart.isEmpty() -> binding.edDateStart.error = "Masukkan tanggal dimulainya pekerjaan"
            dateEnd.isEmpty() -> binding.edDateEnd.error = "Masukkan tanggal berakhirnya pekerjaan"
            jobAdress.isEmpty() -> binding.edLocation.error = "Masukkan alamat lokasi pekerjaan"
            else -> jobViewModel.addJob(uid.toString(), job, publishedJob)
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

        val jobCategoryDropdown = resources.getStringArray(R.array.list_skills)
        val jobCategoryDropdownAdapter = ArrayAdapter(requireContext(), R.layout.layout_dropdown_item, jobCategoryDropdown)
        val spinnerJobCategory = binding.edJobCategory
        spinnerJobCategory.adapter = jobCategoryDropdownAdapter

    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}