package com.amikom.carikerja.ui.bottom_nav.profile

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
import com.amikom.carikerja.adapter.SkillsAdapter
import com.amikom.carikerja.adapter.chooseSkillsListener
import com.amikom.carikerja.databinding.FragmentChooseListSkillsBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.SkillsDetail
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseListSkillsFragment : Fragment(), chooseSkillsListener {

    private var _binding: FragmentChooseListSkillsBinding? = null
    private val binding get() = _binding!!
    private val TAG = "ChooseListSkillsPage"
    private lateinit var skillsAdapter: SkillsAdapter
    private val profileViewModel: ProfileViewModel by viewModels()
    private var uid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseListSkillsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getSkill(uid.toString())
        observe()
        initiateRv()
        checkHasRoleOrNot(uid.toString())

    }

    private fun observe() {

        profileViewModel.getSkillResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    skillsAdapter.setDataSkills(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
                else -> {}
            }
        }

        profileViewModel.insertSkillResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data.toString())
                    goToHomePage()
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
                else -> {}
            }
        }
    }

    override fun btnOnClickChooseSkills(data: ArrayList<SkillsDetail>, position: Int) {
        Log.d(TAG, "btnOnClickChooseSkills: ${skillsAdapter.getSelected()}")
        Log.d(TAG, "btnOnClickChooseSkillsData: $data")

        binding.btnSubmit.setOnClickListener {
            if (skillsAdapter.getSelected().isEmpty() || data.isEmpty()){
                textMessage("Pilih setidaknya salah satu keahlian anda")
            } else {
                profileViewModel.insertSkill(uid.toString(), skills = skillsAdapter.getSelected())
            }
        }
    }

    private fun goToHomePage() {
        if (findNavController().currentDestination?.id == R.id.choose_skills_fragment){
            findNavController().navigate(ChooseListSkillsFragmentDirections.actionChooseSkillsFragmentToNavigationHome())
        }
    }

    private fun checkHasRoleOrNot(uid: String) {
        profileViewModel.checkRole(uid)
        profileViewModel.checkRoleResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage("Berhasil masuk")
                    if (it.data.isNotEmpty()){
                        Log.d(TAG, "checkHasRoleOrNot: ${it.data}")
                        val userRole = it.data
                        val navView = requireActivity().findViewById<BottomNavigationView>(com.amikom.carikerja.R.id.nav_view)
                        when{
                            userRole.toString() == "recruiter" -> {
                                navView.menu.removeItem(com.amikom.carikerja.R.id.navigation_history_work)
                            }
                            userRole.toString() == "worker" -> {
                                navView.menu.removeItem(com.amikom.carikerja.R.id.navigation_history_post_job_work)
                            }
                        }

                        SharedPreferences.saveRole(requireContext(), it.data.toString())
                    }
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
                else -> {}
            }
        }
    }

    private fun initiateRv(){
        val recyclerViewProject: RecyclerView = requireView().findViewById(R.id.rv_skills)
        skillsAdapter = SkillsAdapter(ArrayList())
        skillsAdapter.listenerChooseSkills = this
        recyclerViewProject.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = skillsAdapter
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}