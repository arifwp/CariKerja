package com.amikom.carikerja.ui.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentWorkBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkFragment : Fragment() {

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        initToolbar()


    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                //                R.id.notifications ->
                R.id.settings -> findNavController().navigate(WorkFragmentDirections.actionNavigationDashboardToSettingsFragment())
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}