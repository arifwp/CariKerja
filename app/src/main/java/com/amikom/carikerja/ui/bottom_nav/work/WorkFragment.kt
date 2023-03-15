package com.amikom.carikerja.ui.bottom_nav.work

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.JobAdapter
import com.amikom.carikerja.databinding.FragmentWorkBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkFragment : Fragment() {

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobAdapter: JobAdapter
    private val jobViewModel: JobViewModel by viewModels()
    private var TAG = "WorkFragment"
    private var uid: String? = null
    private var userRole: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateRv()
        uid = SharedPreferences.getUid(requireContext())
        userRole = SharedPreferences.getRole(requireContext())
        Log.d(TAG, "onViewCreated: $userRole")
        when{
            userRole == "recruiter" -> binding.fab.visibility = View.VISIBLE
            userRole == "worker" -> binding.fab.visibility = View.GONE
            else -> binding.fab.visibility = View.GONE
        }
        jobViewModel.getJob(uid.toString())

        val searchJob = binding.searchJob
        val searchCity = binding.searchCity
        binding.searchJob.setIconifiedByDefault(false)
        binding.searchCity.setIconifiedByDefault(false)

        val btnAddWork = binding.fab
        btnAddWork.setOnClickListener {
            findNavController().navigate(WorkFragmentDirections.actionNavigationWorkToAddPostJobFragment())
        }

    }

    private fun observe() {
        jobViewModel.getJobResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    jobAdapter.setJobData(it.data)
                    Log.d(TAG, "observe: ${it.data}")
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
                else -> {}
            }
        }
    }

    private fun initiateRv(){
        val recyclerViewCertificate: RecyclerView = requireView().findViewById(R.id.rv_work)
        jobAdapter = JobAdapter(ArrayList())
        recyclerViewCertificate.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = jobAdapter
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}