package com.amikom.carikerja.ui.bottom_nav.work

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.EducationAdapter
import com.amikom.carikerja.adapter.SkillBottomSheetAdapter
import com.amikom.carikerja.adapter.WorkExperienceAdapter
import com.amikom.carikerja.databinding.BottomSheetApplicantBinding
import com.amikom.carikerja.models.*
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.certificate.CertificateViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.education.EducationViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.project.ProjectViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.work_experience.WorkExperienceViewModel
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import hilt_aggregated_deps._com_amikom_carikerja_ui_bottom_nav_work_DetailJobFragment_GeneratedInjector

@AndroidEntryPoint
class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetApplicantBinding? = null
    private val binding get() = _binding!!
    private val TAG = "BottomSheetFragment"
    private val profileViewModel: ProfileViewModel by viewModels()
    private val workExperienceViewModel: WorkExperienceViewModel by viewModels()
    private val certificateViewModel: CertificateViewModel by viewModels()
    private val projectViewModel: ProjectViewModel by viewModels()
    private val educationViewModel: EducationViewModel by viewModels()
    private val jobViewModel: JobViewModel by viewModels()
    private lateinit var skillBottomSheetAdapter: SkillBottomSheetAdapter
    private lateinit var workExperienceAdapter: WorkExperienceAdapter
    private lateinit var educationAdapter: EducationAdapter
    private var applicant: Applicant? = null
    private var historyJob: HistoryJob? = null
    private var publishedJob: PublishedJob? = null
    private var uid: String? = null
    private var id_jobBtm: String? = null
    private var uidApplicant:String? = null
    private var imageUrlApplicant:String? = null
    private var nameApplicant:String? = null
    private var emailApplicant:String? = null
    private var roleApplicant:String? = null
    private var phoneApplicant:String? = null
    private var dobApplicant:String? = null
    private var addressApplicant:String? = null
    private var summaryApplicant:String? = null
    private var dataWorkExperienceApplicant: MutableList<Exp>? = null
    private var certificateApplicant: MutableList<CertificateDetailString>? = null
    private var projectApplicant: MutableList<ProjectDetails>? = null
    private var educationApplicant: MutableList<EducationDetails>? = null
    private var skillsApplicant: MutableList<SkillsDetail>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetApplicantBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())
        id_jobBtm = DetailJobFragment.id_job.id_job
        observe()
        listener()
        initiateRvSkill()
        initiateRvWorkExp()
        initiateRvEducation()

        profileViewModel.getProfile(uid.toString())
        workExperienceViewModel.getWorkExp(uid.toString())
        certificateViewModel.getCertificate(uid.toString())
        projectViewModel.getProject(uid.toString())
        educationViewModel.getEducation(uid.toString())
        profileViewModel.getUserSkills(uid.toString())
        jobViewModel.getUserIdJob(uid.toString())




    }

    private fun listener(){
//        binding.btnSubmit.setOnClickListener {
//            jobViewModel.getUserIdJob(uid.toString())
//            jobViewModel.getUserIdJobResponse.observe(viewLifecycleOwner){
//                it.getContentIfNotHandled().let {
//                    when(it){
//                        is BaseResponse.Loading -> {}
//                        is BaseResponse.Success -> {
//                            Log.d(TAG, "observe: ${it.data}")
//                            if (it.data.contains(id_jobBtm)){
//                                textMessage("Anda sudah melamar pekerjaan ini")
//                            } else {
//                                jobViewModel.addApplicant(uid.toString(), applicant, historyJob)
//                            }
//                        }
//                        is BaseResponse.Error -> textMessage(it.msg.toString())
//                        else -> {}
//                    }
//                }
//            }
//        }

        binding.btnSubmit.setOnClickListener {
            if (applicant != null){
                jobViewModel.addApplicant(uid.toString(), applicant, historyJob)
            }
        }
    }

    private fun observe() {

        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        uidApplicant = uid.toString()
                        imageUrlApplicant = it.data.imageUrl
                        nameApplicant = it.data.name
                        emailApplicant = it.data.email
                        roleApplicant = it.data.role
                        phoneApplicant = it.data.phone
                        dobApplicant = it.data.dob
                        addressApplicant = it.data.address
                        summaryApplicant = it.data.summary

                        Picasso.get()
                            .load(imageUrlApplicant)
                            .error(R.drawable.dummy_avatar)
                            .into(binding.imgUser)


                        val tvUserName = binding.tvUserName
                        val tvAddress = binding.tvAddress
                        val tvEmail = binding.tvEmail
                        val tvPhone = binding.tvPhone
                        val tvSummary = binding.tvSummary
                        tvUserName.text = it.data.name
                        tvAddress.text = it.data.address
                        tvEmail.text = it.data.email
                        tvPhone.text = it.data.phone
                        tvSummary.text = it.data.summary

                        applicant = Applicant(
                            id_job = id_jobBtm,
                            uid = uid.toString(),
                            id_applicant = null,
                            imageUrl = it.data.imageUrl,
                            name = it.data.name,
                            email = it.data.email,
                            role = it.data.role,
                            phone = it.data.phone,
                            dob = it.data.dob,
                            address = it.data.address,
                            summary = it.data.summary,
                            dataWorkExperience = null,
                            certificate = null,
                            project = null,
                            education = null
                        )
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }

        workExperienceViewModel.getWorkExp.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        dataWorkExperienceApplicant = it.data
                        workExperienceAdapter.setWorkExpData(it.data)
                        applicant = Applicant(
                            id_job = id_jobBtm,
                            uid = uidApplicant,
                            id_applicant = null,
                            imageUrl = imageUrlApplicant,
                            name = nameApplicant,
                            email = emailApplicant,
                            role = roleApplicant,
                            phone = phoneApplicant,
                            dob = dobApplicant,
                            address = addressApplicant,
                            summary = summaryApplicant,
                            dataWorkExperience = it.data,
                            certificate = null,
                            project = null,
                            education = null
                        )
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }

        certificateViewModel.getCertificateResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    certificateApplicant = it.data
                    applicant = Applicant(
                        id_job = id_jobBtm,
                        uid = uidApplicant,
                        id_applicant = null,
                        imageUrl = imageUrlApplicant,
                        name = nameApplicant,
                        email = emailApplicant,
                        role = roleApplicant,
                        phone = phoneApplicant,
                        dob = dobApplicant,
                        address = addressApplicant,
                        summary = summaryApplicant,
                        dataWorkExperience = dataWorkExperienceApplicant,
                        certificate = it.data,
                        project = null,
                        education = null
                    )
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        projectViewModel.getProjectResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    projectApplicant = it.data
                    applicant = Applicant(
                        id_job = id_jobBtm,
                        uid = uidApplicant,
                        id_applicant = null,
                        imageUrl = imageUrlApplicant,
                        name = nameApplicant,
                        email = emailApplicant,
                        role = roleApplicant,
                        phone = phoneApplicant,
                        dob = dobApplicant,
                        address = addressApplicant,
                        summary = summaryApplicant,
                        dataWorkExperience = dataWorkExperienceApplicant,
                        certificate = certificateApplicant,
                        project = it.data,
                        education = null
                    )
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())

            }
        }

        educationViewModel.getEducationResponse.observe(viewLifecycleOwner){ observe ->
            observe.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        educationApplicant = it.data
                        educationAdapter.setEducationData(it.data)
                        applicant = Applicant(
                            id_job = id_jobBtm,
                            uid = uidApplicant,
                            id_applicant = null,
                            imageUrl = imageUrlApplicant,
                            name = nameApplicant,
                            email = emailApplicant,
                            role = roleApplicant,
                            phone = phoneApplicant,
                            dob = dobApplicant,
                            address = addressApplicant,
                            summary = summaryApplicant,
                            dataWorkExperience = dataWorkExperienceApplicant,
                            certificate = certificateApplicant,
                            project = projectApplicant,
                            education = it.data
                        )
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }

        profileViewModel.getUserSkillsResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    skillsApplicant = it.data
                    skillBottomSheetAdapter.setSkillData(it.data)
                    applicant = Applicant(
                        id_job = id_jobBtm,
                        uid = uidApplicant,
                        id_applicant = null,
                        imageUrl = imageUrlApplicant,
                        name = nameApplicant,
                        email = emailApplicant,
                        role = roleApplicant,
                        phone = phoneApplicant,
                        dob = dobApplicant,
                        address = addressApplicant,
                        summary = summaryApplicant,
                        dataWorkExperience = dataWorkExperienceApplicant,
                        certificate = certificateApplicant,
                        project = projectApplicant,
                        education = educationApplicant,
                        skills = it.data
                    )
                    historyJob = HistoryJob(
                        id_job = id_jobBtm
                    )

                }
                else -> {}
            }
        }

        jobViewModel.addAppplicantResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    dialog?.dismiss()
                    textMessage(it.data.toString())
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        jobViewModel.getUserIdJobResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        Log.d(TAG, "observe: ${it.data}")
                        when{
                            it.data.isNullOrEmpty() -> {
                                binding.wrapBtnSubmit.visibility = View.VISIBLE
                                binding.wrapTextApplied.visibility = View.GONE
                            }
                            it.data.contains(id_jobBtm) -> {
                                binding.wrapBtnSubmit.visibility = View.GONE
                                binding.wrapTextApplied.visibility = View.VISIBLE
                            }
                            else -> {
                                binding.wrapBtnSubmit.visibility = View.VISIBLE
                                binding.wrapTextApplied.visibility = View.GONE
                            }
                        }
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }

    }

    private fun initiateRvSkill(){
        val recyclerViewSkill: RecyclerView = requireView().findViewById(R.id.rv_skills_bottom_sheet)
        skillBottomSheetAdapter = SkillBottomSheetAdapter(ArrayList())
        recyclerViewSkill.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = skillBottomSheetAdapter
        }
    }

    private fun initiateRvWorkExp(){
        val recyclerViewWorkExp: RecyclerView = requireView().findViewById(R.id.rv_work_exp)
        workExperienceAdapter = WorkExperienceAdapter(ArrayList())
        recyclerViewWorkExp.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = workExperienceAdapter
        }
    }

    private fun initiateRvEducation() {
        val recyclerViewEducation: RecyclerView = requireView().findViewById(R.id.rv_education_bottom_sheet)
        educationAdapter = EducationAdapter(ArrayList())
        recyclerViewEducation.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = educationAdapter
        }
    }


    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}