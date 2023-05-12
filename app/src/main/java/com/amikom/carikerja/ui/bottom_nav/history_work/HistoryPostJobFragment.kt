package com.amikom.carikerja.ui.bottom_nav.history_work

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.PublishedJobAdapter
import com.amikom.carikerja.databinding.FragmentHistoryPostJobBinding
import com.amikom.carikerja.databinding.FragmentProfileBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryPostJobFragment : Fragment() {

    private var _binding: FragmentHistoryPostJobBinding? = null
    private val binding get() = _binding!!
    private var TAG = "HistoryPostJobFragment"
    private val historyJobViewModel: HistoryJobViewModel by viewModels()
    private lateinit var publishedJobAdapter: PublishedJobAdapter
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryPostJobBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())

        historyJobViewModel.getJobByUser(uid.toString())
        observe()
        initiateRv()

    }

    private fun initiateRv() {
        val recyclerViewPublishedJob: RecyclerView = requireView().findViewById(R.id.rv_published_job)
        publishedJobAdapter = PublishedJobAdapter(ArrayList())
        recyclerViewPublishedJob.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = publishedJobAdapter
        }
    }

    private fun observe() {
        historyJobViewModel.getJobByUserResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    when{
                        it.data.isNullOrEmpty() -> {
                            binding.wrapEmptyPublishedJob.visibility = View.VISIBLE
                            binding.wrapRvPublishedJob.visibility = View.GONE
                        }
                        else -> {
                            publishedJobAdapter.setPublishedJobData(it.data)
                            binding.wrapRvPublishedJob.visibility = View.VISIBLE
                            binding.wrapEmptyPublishedJob.visibility = View.GONE
                        }
                    }
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
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