package com.amikom.carikerja.ui.bottom_nav.history_work

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.HistoryWorkAdapter
import com.amikom.carikerja.adapter.btnClickHistoryWork
import com.amikom.carikerja.databinding.FragmentHistoryWorkBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.ui.bottom_nav.work.post_job.JobViewModel
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryWorkFragment : Fragment(), btnClickHistoryWork {

    private var _binding: FragmentHistoryWorkBinding? = null
    private val binding get() = _binding!!
    private var TAG = "HistoryWorkFragment"
    private var uid: String? = null
    private val historyJobViewModel: HistoryJobViewModel by viewModels()
    private val jobViewModel: JobViewModel by viewModels()
    private lateinit var historyWorkAdapter: HistoryWorkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryWorkBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())

        historyJobViewModel.getHistoryWorker(uid.toString())
        observe()
        initiateRv()

        val btnSearchJob = binding.btnSearch
        btnSearchJob.setOnClickListener {
            findNavController().navigate(HistoryWorkFragmentDirections.actionNavigationHistoryWorkToNavigationWork())
        }


    }

    override fun btnClickItem(idJob: String?) {
        findNavController().navigate(HistoryWorkFragmentDirections.actionNavigationHistoryWorkToDetailJobFragment(
            idJob,
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
            null
        ))
    }

    private fun observe() {
        historyJobViewModel.getHistoryWorkerResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success  ->{
                    when{
                        it.data.isNullOrEmpty() -> {
                            binding.wrapEmptyHistory.visibility = View.VISIBLE
                            binding.wrapRvHistoryWork.visibility = View.GONE
                        }
                        else -> {
                            historyWorkAdapter.setHistoryWorkData(it.data)
                            binding.wrapRvHistoryWork.visibility = View.VISIBLE
                            binding.wrapEmptyHistory.visibility = View.GONE
                        }
                    }
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun initiateRv() {
        val recyclerViewHistory: RecyclerView = requireView().findViewById(R.id.rv_history_work)
        historyWorkAdapter = HistoryWorkAdapter(requireContext(), ArrayList())
        historyWorkAdapter.listenerBtnHistory = this
        recyclerViewHistory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = historyWorkAdapter
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