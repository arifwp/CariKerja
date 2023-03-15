package com.amikom.carikerja.ui.bottom_nav.history_work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.databinding.FragmentHistoryWorkBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryWorkFragment : Fragment() {

    private var _binding: FragmentHistoryWorkBinding? = null
    private val binding get() = _binding!!
    private var TAG = "HistoryWorkFragment"
    private var uid: String? = null

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

        val btnSearchJob = binding.btnSearch
        btnSearchJob.setOnClickListener {
            findNavController().navigate(HistoryWorkFragmentDirections.actionNavigationHistoryWorkToNavigationWork())
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