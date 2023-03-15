package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.education

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.databinding.FragmentAddEducationBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.EducationDetails
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.log

@AndroidEntryPoint
class AddEducationFragment : Fragment() {

    private var _binding: FragmentAddEducationBinding? = null
    private val binding get() = _binding!!
    private val educationViewModel: EducationViewModel by viewModels()
    private var TAG = "AddEducationFragment"
    private var uid: String? = null
    private val args: AddEducationFragmentArgs by navArgs()
    private var id: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddEducationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initiateInputComponent()
        observe()
        val btnSubmit = binding.btnSubmit
        uid = SharedPreferences.getUid(requireContext())
        id = args.id

        Log.d(TAG, "onViewCreated: $id")
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

        val institution = binding.institution
        val degree = binding.edDegree
        val fieldStudy = binding.edFieldOfStudy
        val dateStart = binding.edDateStart
        val dateEnd = binding.edDateEnd
        val description = binding.edDescription

        institution.setText(args.institution)
        degree.setText(args.degree)
        fieldStudy.setText(args.fieldOfStudy)
        dateStart.setText(args.dateStart)
        dateEnd.setText(args.dateEnd)
        description.setText(args.description)

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            educationViewModel.editEducation(
                uid.toString(),
                args.id.toString(),
                institution.text.toString().trim(),
                degree.text.toString().trim(),
                fieldStudy.text.toString().trim(),
                dateStart.text.toString().trim(),
                dateEnd.text.toString().trim(),
                description.text.toString().trim()
            )
        }

        val btnDelete = binding.deleteData
        btnDelete.setOnClickListener {
            educationViewModel.deleteEducation(
                uid.toString(),
                args.id.toString(),
            )
        }
    }

    private fun observe() {
        educationViewModel.addEducationResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> textMessage(it.data)
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
        educationViewModel.editEducationResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> textMessage(it.data)
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
        educationViewModel.deleteEducationResponse.observe(viewLifecycleOwner){
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

    private fun validateData() {
        val institution = binding.institution.text.toString().trim()
        val degree = binding.edDegree.text.toString().trim()
        val fieldStudy = binding.edFieldOfStudy.text.toString().trim()
        val dateStart = binding.edDateStart.text.toString().trim()
        val dateEnd = binding.edDateEnd.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()

        val dataEdu = EducationDetails(
            id = null,
            institution = institution,
            degree = degree,
            field_of_study = fieldStudy,
            dateStart = dateStart,
            dateEnd = dateEnd,
            description = description
        )

        if (institution.isEmpty()) {
            binding.institution.error = "Masukkan nama institusi"
        }

        if (dateStart.isEmpty()) {
            binding.edDateStart.error = "Masukkan keterangan tanggal"
        }

        if (dateEnd.isEmpty()){
            binding.edDateStart.error = "Masukkan keterangan tanggal"
        }

        if (institution.isNotEmpty() || dateStart.isNotEmpty() || dateEnd.isNotEmpty()){
            educationViewModel.addEducation(uid.toString(), dataEdu)
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

    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}