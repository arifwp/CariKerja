package com.amikom.carikerja.ui.bottom_nav.work

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.EducationAdapter
import com.amikom.carikerja.adapter.SkillBottomSheetAdapter
import com.amikom.carikerja.adapter.WorkExperienceAdapter
import com.amikom.carikerja.databinding.FragmentDetailJobBinding
import com.amikom.carikerja.models.*
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.certificate.CertificateViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.education.EducationViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.project.ProjectViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.work_experience.WorkExperienceViewModel
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailJobFragment : Fragment() {

    private var _binding: FragmentDetailJobBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DetailJobFragment"
    private val args: DetailJobFragmentArgs by navArgs()
    private val jobViewModel: JobViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val workExperienceViewModel: WorkExperienceViewModel by viewModels()
    private val certificateViewModel: CertificateViewModel by viewModels()
    private val projectViewModel: ProjectViewModel by viewModels()
    private val educationViewModel: EducationViewModel by viewModels()
    private lateinit var skillBottomSheetAdapter: SkillBottomSheetAdapter
    private lateinit var workExperienceAdapter: WorkExperienceAdapter
    private lateinit var educationAdapter: EducationAdapter
    private var uid: String? = null
    private var applicant: Applicant? = null
    private var id: String? = null
    private var uid_person_who_post: String? = null
    private var job_title: String? = null
    private var person_who_post: String? = null
    private var image_url: String? = null
    private var date_start: String? = null
    private var date_end: String? = null
    private var total_day: String? = null
    private var job_description: String? = null
    private var job_category: String? = null
    private var employee_type: String? = null
    private var job_address: String? = null
    private var salary: String? = null
    private var post_time: String? = null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailJobBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())

        initiateComponent()
        observe()
        initiateRvSkill()
        initiateRvWorkExp()
        initiateRvEducation()
        listener()

    }

    private fun initiateComponent() {
        id = args.id
        uid_person_who_post = args.uid
        job_title = args.jobTitle
        person_who_post = args.personWhoPost
        image_url = args.imageUrl
        date_start = args.dateStart
        date_end = args.dateEnd
        total_day = args.totalDay
        job_description = args.jobDescription
        job_category = args.jobCategory
        employee_type = args.employeeType
        job_address = args.jobAddress
        salary = args.salary
        post_time = args.postTime

        val avatar = binding.userImage
        binding.tvJobTitle.text = job_title
        binding.tvUserName.text = person_who_post
        binding.tvSalary.text = salary
        binding.tvEmployeeType.text = employee_type
        binding.tvDateStart.text = date_start
        binding.tvDateEnd.text = date_end
        binding.tvTotalDay.text = total_day
        binding.tvJobCategory.text = job_category
        binding.tvTimePost.text = post_time
        binding.tvJobAddress.text = job_address
        binding.tvJobDescription.text = job_description

        Picasso.get()
            .load(args.imageUrl)
            .error(R.drawable.dummy_avatar)
            .into(avatar)

        profileViewModel.getProfile(uid.toString())
        workExperienceViewModel.getWorkExp(uid.toString())
        certificateViewModel.getCertificate(uid.toString())
        projectViewModel.getProject(uid.toString())
        educationViewModel.getEducation(uid.toString())
        profileViewModel.getUserSkills(uid.toString())

    }

    private fun listener() {
        val btnApply = binding.btnApply
        btnApply.setOnClickListener {
            showDialog()
        }

    }

    private fun showDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_applicant, null)


        initiateVariableDialog(bottomSheet)

        dialog.setCancelable(true)
        dialog.setContentView(bottomSheet)

        dialog.show()
    }

    private fun initiateVariableDialog(bottomSheet: View) {
        val userImg = bottomSheet.findViewById<ImageView>(R.id.img_user)
        val tvUserName = bottomSheet.findViewById<TextView>(R.id.tv_user_name)
        val tvAddress = bottomSheet.findViewById<TextView>(R.id.tv_address)
        val tvEmail = bottomSheet.findViewById<TextView>(R.id.tv_email)
        val tvPhone = bottomSheet.findViewById<TextView>(R.id.tv_phone)
        val tvSummary = bottomSheet.findViewById<TextView>(R.id.tv_summary)
        val rvWorkExp = bottomSheet.findViewById<RecyclerView>(R.id.rv_work_exp)
        val rvEducation = bottomSheet.findViewById<RecyclerView>(R.id.rv_education)
        val rvProject = bottomSheet.findViewById<RecyclerView>(R.id.rv_project)
        val rvCertificate = bottomSheet.findViewById<RecyclerView>(R.id.rv_certificate)

        Picasso.get()
            .load(imageUrlApplicant)
            .error(R.drawable.dummy_avatar)
            .into(userImg)

        tvUserName.text = nameApplicant
        tvAddress.text = addressApplicant
        tvEmail.text = emailApplicant
        tvPhone.text = phoneApplicant
        tvSummary.text = summaryApplicant



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

                        applicant = Applicant(
                            uid = uid.toString(),
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
                        Log.d(TAG, "observeGetProfile: ${it.data}")
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
                        Log.d(TAG, "observeWorkExp: ${it.data}")
                        applicant = Applicant(
                            uid = uidApplicant,
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
                        uid = uidApplicant,
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
                        uid = uidApplicant,
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
                        Log.d(TAG, "observeEducation: ${it.data}")
                        applicant = Applicant(
                            uid = uidApplicant,
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
                    Log.d(TAG, "observeSkill: ${it.data}")
//                    skillBottomSheetAdapter.setSkillData(it.data)
                    applicant = Applicant(
                        uid = uidApplicant,
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
                }
                else -> {}
            }
        }

    }

    private fun initiateRvSkill(){
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_applicant, null)
        dialog.setContentView(bottomSheet)

        val recyclerViewSkill: RecyclerView? = dialog.findViewById(R.id.rv_skills_bottom_sheet)
        skillBottomSheetAdapter = SkillBottomSheetAdapter(ArrayList())
        recyclerViewSkill?.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = skillBottomSheetAdapter
        }
    }

    private fun initiateRvWorkExp(){
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_applicant, null)
        val recyclerViewWorkExp: RecyclerView = bottomSheet.findViewById(R.id.rv_work_exp)
        workExperienceAdapter = WorkExperienceAdapter(ArrayList())
        recyclerViewWorkExp.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = workExperienceAdapter
        }
    }

    private fun initiateRvEducation() {
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_applicant, null)
        val recyclerViewEducation: RecyclerView = bottomSheet.findViewById(R.id.rv_education_bottom_sheet)
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