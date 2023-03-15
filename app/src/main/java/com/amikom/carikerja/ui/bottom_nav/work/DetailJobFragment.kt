package com.amikom.carikerja.ui.bottom_nav.work

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentDetailJobBinding
import com.amikom.carikerja.models.*
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.certificate.CertificateViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.education.EducationViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.project.ProjectViewModel
import com.amikom.carikerja.ui.bottom_nav.profile.profile_details.work_experience.WorkExperienceViewModel
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
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
        listener()
        observe()
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

    }

    private fun listener() {
        val btnApply = binding.btnApply
        btnApply.setOnClickListener {
            profileViewModel.getProfile(uid.toString())
            workExperienceViewModel.getWorkExp(uid.toString())
            certificateViewModel.getCertificate(uid.toString())
            projectViewModel.getProject(uid.toString())
            educationViewModel.getEducation(uid.toString())
            profileViewModel.getUserSkills(uid.toString())
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
                    Log.d(TAG, "observeGetUserSkills: ${it.data}")
                    Log.d(TAG, "observeAfterAdd: $applicant")
                }
                else -> {}
            }
        }

    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}