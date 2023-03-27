package com.amikom.carikerja.ui.bottom_nav.history_work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.adapter.ListApplicantAdapter
import com.amikom.carikerja.adapter.showBottomSheet
import com.amikom.carikerja.databinding.FragmentListApplicantBinding
import com.amikom.carikerja.models.Applicant
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.ui.bottom_nav.work.BottomSheetFragment
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListApplicantFragment : Fragment(), showBottomSheet {

    private var _binding: FragmentListApplicantBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ListApplicantFragment"
    private val historyJobViewModel: HistoryJobViewModel by viewModels()
    private val args: ListApplicantFragmentArgs by navArgs()
    private lateinit var listApplicantAdapter: ListApplicantAdapter
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListApplicantBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())
        historyJobViewModel.getApplicant(uid.toString(), args.id.toString())

        observe()
        initiateRv()

    }

    override fun btnItem(data: Applicant) {
        showBottomSheetDialogFragment(data.uid)
    }

    private fun observe() {
        historyJobViewModel.getApplicantResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    listApplicantAdapter.setApplicantData(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun initiateRv() {
        val recyclerViewApplicant: RecyclerView = requireView().findViewById(R.id.rv_list_applicant)
        listApplicantAdapter = ListApplicantAdapter(requireContext(), ArrayList())
        listApplicantAdapter.listenerBtnItem = this
        recyclerViewApplicant.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = listApplicantAdapter
        }
    }

    private fun showBottomSheetDialogFragment(uid: String?) {
        val bottomSheetFragment = BottomSheetFragment(uid)
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}