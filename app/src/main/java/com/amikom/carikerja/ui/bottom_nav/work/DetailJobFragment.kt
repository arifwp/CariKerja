package com.amikom.carikerja.ui.bottom_nav.work

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
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentDetailJobBinding
import com.amikom.carikerja.models.*
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.utils.SharedPreferences
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

        jobViewModel.getUserIdJob(uid.toString())

        observe()

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        when{
            userRole == "worker" -> binding.wrapBtnApply.visibility = View.VISIBLE
            userRole == "recruiter" -> binding.wrapBtnApply.visibility = View.GONE
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
                        Log.d(TAG, "observe: ${it.data}")
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
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }
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

    private fun showBottomSheetDialogFragment() {
        val bottomSheetFragment = BottomSheetFragment()
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}