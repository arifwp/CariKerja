package com.amikom.carikerja.ui.bottom_nav.work

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.amikom.carikerja.models.JobDetails
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.ui.notification.NotificationViewModel
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class WorkFragment : Fragment() {

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobAdapter: JobAdapter
    private val jobViewModel: JobViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()
    private var TAG = "WorkFragment"
    private var uid: String? = null
    private var userRole: String? = null
    private var nameRecruiter: String? = null
    private var imgUrlRecruiter: String? = null
    var spinnerItem: String? = null
    var data_list_job: List<JobDetails>? = null
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAK-WQOKE:APA91bEwAvh5BbO6DrATRGN6zy9dF0QFG26Wd_83JpC-0UcYe4RCieRMfWCScXNJnBIRhAOHHCKGH6D358wC3SVVPBxKEMmv35vBGFaRbdqbe7DLNwOBsjW_1Gyh9DWvlsIeoIPmfXkb"
    private val contentType = "application/json"
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(requireContext())
    }


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
        when{
            userRole == "recruiter" -> binding.fab.visibility = View.VISIBLE
            userRole == "worker" -> binding.fab.visibility = View.GONE
            else -> binding.fab.visibility = View.GONE
        }

        jobViewModel.getJob(uid.toString())
        profileViewModel.getSkill(uid.toString())

        val btnAddWork = binding.fab
        btnAddWork.setOnClickListener {


//            val registration_ids = arrayOf(
//                "eqOnc2b-TCCO323VeYsKbj:APA91bEIPUHkaNChtwWv3BKtWd0rhmDFPOrORiSdDTrEAeVOqwiKoU8Z8hH0u0E2k1iwknzswQhup1WVwx68IJwKNedmf9UrGliAmQibE5TQj2kZFqphrRQo6icRGN1jThoDAH7SaJ--",
//                "fNaT68nlQ56Hx0FrSHOSCZ:APA91bHIgWnrPdUnmeB7T2gmmdQZPL_JTETdQw4RUqJfpOJPQQip_t9IYgXgSuGm_SJpfUXMpEKo3nfiUm9AkS1EwShfQb7_Ti_P5CFd05pWeKpHYPqi-inW-KhykthJZR1YPfuPquxu"
//            )
//
//            notificationViewModel.postNotification(registration_ids, "Retrofit Success", "mengirim telah sukses")
//            notificationViewModel.postNotificationResponse.observe(viewLifecycleOwner){
//                it.getContentIfNotHandled()?.let {
//                    when(it){
//                        is BaseResponse.Loading -> {}
//                        is BaseResponse.Success -> {
//                            textMessage(it.data)
//                        }
//                        is BaseResponse.Error -> textMessage(it.msg.toString())
//                    }
//                }
//            }

//            val notification = JSONObject()
//            val notifcationBody = JSONObject()
//
//            try {
//                notifcationBody.put("title", "Bukber nanti sore!")
//                notifcationBody.put("body", "pesan deskripsi membawa berita bukber")   //Enter your notification message
//                notification.put("to", regis_id)
//                notification.put("data", notifcationBody)
//                Log.e("TAG", "try")
//            } catch (e: JSONException) {
//                Log.e("TAG", "onCreate: " + e.message)
//            }
//
//            sendNotification(notification)

            findNavController().navigate(WorkFragmentDirections.actionNavigationWorkToAddPostJobFragment(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
            ))
        }

    }

    private fun sendNotification(notification: JSONObject){
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
//                msg.setText("")
            },
            Response.ErrorListener {
//                Toast.makeText(this@MainActivity, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }


    private fun searchByJobCategory(data_spinner: String, dataJob: MutableList<JobDetails>) {

        spinnerItem = data_spinner
        data_list_job = dataJob

        if (data_spinner != "Semua"){
            val categoryList: List<JobDetails>? = jobAdapter.filterJobCategory(dataJob, data_spinner)
            if (categoryList != null){
                jobAdapter.setJobData(categoryList)
            }
        }
    }

    private fun searchByJobTitle(data: MutableList<JobDetails>) {
        val searchJobTitle = binding.searchJob
        searchJobTitle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val newList: List<JobDetails>? = jobAdapter.filterCars(data, binding.searchJob.text.toString().trim())
                if (newList != null) {
                    jobAdapter.setJobData(newList)
                }

                if (spinnerItem != "Semua"){
                    val categoryList: List<JobDetails>? = jobAdapter.filterJobCategory(data_list_job, spinnerItem)
                    if (categoryList != null){
                        jobAdapter.setJobData(categoryList)
                    }
                }


            }

        })
    }


    private fun observe() {
        jobViewModel.getJobResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    searchByJobTitle(it.data)

                    val dataJob = it.data

                    profileViewModel.getSkillResponse.observe(viewLifecycleOwner){
                        it.getContentIfNotHandled()?.let {
                            when(it){
                                is BaseResponse.Loading -> {}
                                is BaseResponse.Success -> {
                                    if (it.data != null){
                                        val response = it.data.map { it.name }
                                        val adapter = ArrayAdapter<String>(
                                            requireContext(),
                                            R.layout.layout_dropdown_item
                                        )

                                        adapter.add("Semua")
                                        adapter.addAll(response)
                                        adapter.setDropDownViewResource(R.layout.layout_dropdown_item)
                                        binding.edJobCategory.adapter = adapter
                                        binding.edJobCategory.onItemSelectedListener

                                        binding.edJobCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                            override fun onItemSelected(
                                                parent: AdapterView<*>?,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val data_spinner = binding.edJobCategory.selectedItem.toString().trim()
                                                searchByJobCategory(data_spinner, dataJob)
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>?) {

                                            }

                                        }



                                    }
                                }
                                is BaseResponse.Error -> textMessage(it.msg.toString())
                            }
                        }
                    }

                    jobAdapter.setJobData(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
                else -> {}
            }
        }



        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled()?.let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        nameRecruiter = it.data.name
                        imgUrlRecruiter = it.data.imageUrl
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                }
            }
        }
    }

    private fun initiateRv(){
        val recyclerViewCertificate: RecyclerView = requireView().findViewById(R.id.rv_work)
        jobAdapter = JobAdapter(ArrayList())
        recyclerViewCertificate.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
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