package com.amikom.carikerja.ui.bottom_nav.work

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentDetailJobBinding
import com.amikom.carikerja.models.*
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.utils.TimeShow
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DetailJobFragment : Fragment() {
    object id_job {
        var id_job: String? = null
    }

    private var _binding: FragmentDetailJobBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DetailJobFragment"
    private val args: DetailJobFragmentArgs by navArgs()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val jobViewModel: JobViewModel by viewModels()
    private var userRole: String? = null
    private var uid: String? = null
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
    private var job_status: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailJobBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())
        userRole = SharedPreferences.getRole(requireContext())


        if (args.personWhoPost.isNullOrEmpty()){
            jobViewModel.getDetailJob(args.id)
        }

        observe()
        jobViewModel.getTotalApplicant(uid.toString(), args.id.toString())

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        when{
            userRole == "worker" -> {
                jobViewModel.getUserIdJob(uid.toString())

                when {
                    args.jobStatus == "closed" -> {
                        binding.wrapJobClosed.visibility = View.VISIBLE
                        binding.wrapBtnSubmit.visibility = View.GONE
                    }
                    args.jobStatus == "open" -> {
                        binding.wrapJobClosed.visibility = View.GONE
                        binding.wrapBtnSubmit.visibility = View.VISIBLE
                    }
                }

            }
            userRole == "recruiter" -> {
                jobViewModel.getPublishedJobByRecruiterUid(uid.toString())
                jobViewModel.getJobStatus(id_job.id_job)
                binding.wrapBtnApply.visibility = View.GONE
            }
            else -> binding.wrapBtnApply.visibility = View.GONE
        }

        initiateComponent()
        listener()

    }

    private fun observe() {
        jobViewModel.getUserIdJobResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        when{
                            it.data.isNullOrEmpty() -> {
                                binding.wrapBtnSubmit.visibility = View.VISIBLE
                                binding.wrapTextApplied.visibility = View.GONE
                            }
                            it.data.contains(id_job.id_job) -> {
                                binding.wrapBtnSubmit.visibility = View.GONE
                                binding.wrapTextApplied.visibility = View.VISIBLE
                            }
                            else -> {
                                binding.wrapBtnSubmit.visibility = View.VISIBLE
                                binding.wrapTextApplied.visibility = View.GONE
                            }
                        }
                    }
                    is BaseResponse.Error -> {
                        textMessage(it.msg.toString())
                    }
                    else -> {}
                }
            }
        }

        jobViewModel.getJobStatusResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        job_status = it.data
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }

        jobViewModel.getPublishedJobByRecruiterUidResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        if (it.data.map { it.id }.contains(args.id)){


                            jobViewModel.getTotalApplicant(uid.toString(), args.id.toString())
                            jobViewModel.getTotalApplicantResponse.observe(viewLifecycleOwner){
                                when(it){
                                    is BaseResponse.Loading -> {}
                                    is BaseResponse.Success -> {
                                        binding.tvTotalApplicant.text = it.data.toString()
                                        if (it.data != 0){
                                            binding.wrapTotalApplicant.setOnClickListener {
                                                findNavController().navigate(DetailJobFragmentDirections.actionDetailJobFragmentToListApplicantFragment(args.id))
                                            }
                                        }
                                    }
                                    is BaseResponse.Error -> textMessage(it.msg.toString())
                                }
                            }

                            val icDelete = binding.icDelete
                            icDelete.visibility = View.VISIBLE
                            icDelete.setOnClickListener {
                                showAlertDialog("delete")
                            }


                            val icEdit = binding.icEdit
                            icEdit.visibility = View.VISIBLE
                            icEdit.setOnClickListener {
                                showAlertDialog("edit")
//                                findNavController().navigate(DetailJobFragmentDirections.actionDetailJobFragmentToAddPostJobFragment(
//                                    args.id,
//                                    args.uid,
//                                    args.jobTitle,
//                                    args.personWhoPost,
//                                    args.imageUrl,
//                                    args.dateStart,
//                                    args.dateEnd,
//                                    args.totalDay,
//                                    args.jobDescription,
//                                    args.jobCategory,
//                                    args.employeeType,
//                                    args.jobAddress,
//                                    args.salary,
//                                    args.postTime,
//                                    job_status
//                                ))
                            }

                            binding.wrapTotalApplicant.visibility = View.VISIBLE


                        } else {
                            binding.wrapTotalApplicant.visibility = View.GONE
                        }
                    }
                    is BaseResponse.Error -> {
                        textMessage(it.msg.toString())
                    }
                    else -> {}
                }
            }
        }

        jobViewModel.getJobDetailResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        binding.tvJobTitle.text = it.data.job_title
                        binding.tvUserName.text = it.data.person_who_post
                        binding.tvSalary.text = it.data.salary
                        binding.tvEmployeeType.text = it.data.employee_type
                        binding.tvDateStart.text = it.data.dateStart
                        binding.tvDateEnd.text = it.data.dateEnd
                        binding.tvJobCategory.text = it.data.job_category
                        binding.tvJobAddress.text = it.data.job_address
                        binding.tvJobDescription.text = it.data.job_description
                        binding.tvTotalDay.text = it.data.total_day

                        // Time Ago
                        val timeDetail: String? = it.data.post_time
                        val timeAgo2Detail = TimeShow()
                        val myFinalValueDetail: String = timeAgo2Detail.covertTimeToText(timeDetail).toString()
                        binding.tvTimePost.text = myFinalValueDetail

                        Picasso.get()
                            .load(it.data.image_url)
                            .error(R.drawable.dummy_avatar)
                            .into(binding.userImage)
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }

        jobViewModel.getTotalApplicantResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    binding.tvJobApplied.text = it.data.toString()
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        jobViewModel.deleteJobResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        findNavController().popBackStack()
                        textMessage(it.data)
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }
    }

    private fun showAlertDialog(action: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_alert)
        val btnCancel = dialog.findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        val tvTitleAlert = dialog.findViewById<TextView>(R.id.tv_title_alert)
        val btnExecute = dialog.findViewById<Button>(R.id.btn_execute)

        when(action){
            "delete" -> {
                btnExecute.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red_400)
                btnExecute.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                btnExecute.text = "Hapus Data"
                tvTitleAlert.text = "Apakah anda yakin ingin menghapus data pekerjaan ${args.jobTitle} yang sudah anda publish?"
                btnExecute.setOnClickListener {
                    dialog.dismiss()
                    jobViewModel.deleteJob(uid.toString(), args.id.toString())
                }
            }

            "edit" -> {
                btnExecute.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.yellow_700)
                btnExecute.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                btnExecute.text = "Ubah Data"
                tvTitleAlert.text = "Apakah anda yakin ingin mengubah data pekerjaan ${args.jobTitle} yang sudah anda publish?"
                btnExecute.setOnClickListener {

                    dialog.dismiss()
                    goToEditDataPage()
                }
            }
        }

        dialog.setCancelable(true)
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initiateComponent() {
        id_job.id_job = args.id
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

    }

    private fun listener() {
        val btnApply = binding.btnApply

        btnApply.setOnClickListener {
            showBottomSheetDialogFragment()
        }

    }

    private fun goToEditDataPage(){
        findNavController().navigate(DetailJobFragmentDirections.actionDetailJobFragmentToAddPostJobFragment(
            args.id,
            args.uid,
            args.jobTitle,
            args.personWhoPost,
            args.imageUrl,
            args.dateStart,
            args.dateEnd,
            args.totalDay,
            args.jobDescription,
            args.jobCategory,
            args.employeeType,
            args.jobAddress,
            args.salary,
            args.postTime,
            job_status
        ))
    }

    private fun showBottomSheetDialogFragment() {
        val bottomSheetFragment = BottomSheetFragment(null, args.uid, args.personWhoPost, args.jobTitle)
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}