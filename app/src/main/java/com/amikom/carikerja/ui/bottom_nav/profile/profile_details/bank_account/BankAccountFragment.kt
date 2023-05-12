package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.bank_account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentBankAccountBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankAccountFragment : Fragment() {

    private var _binding: FragmentBankAccountBinding? = null
    private val binding get() = _binding!!
    private val TAG = "BankAccountFragment"
    private val bankAccountViewModel: BankAccountViewModel by viewModels()
    private var uid: String? = null
    private val args: BankAccountFragmentArgs by navArgs()
    private var result: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBankAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())

        setUiData()
        binding.btnSubmit.setOnClickListener {
            if (result != null){
                textMessage("Berhasil mengubah data")
            }
            editData()
        }

        val icBack = binding.icBack
        icBack.setOnClickListener {
            findNavController().popBackStack()
        }

        observe()
        initiateComponent()
    }

    private fun setUiData() {

        if (args.bankName != "null") {
            binding.autoTvBankName.setText(args.bankName)
        }
        if (args.bankAccount != "null"){
            binding.edBankAccount.setText(args.bankAccount)
        }
        if (args.bankAccountName != "null"){
            binding.edBankAccountName.setText(args.bankAccountName)
        }

    }

    private fun editData() {
        val tvBankName = binding.autoTvBankName.text.toString().trim()
        val tvBankAccount = binding.edBankAccount.text.toString().trim()
        val tvBankAccountName = binding.edBankAccountName.text.toString().trim()

        bankAccountViewModel.editBankDetail(uid.toString(), tvBankName, tvBankAccount, tvBankAccountName)

    }

    private fun observe() {

        bankAccountViewModel.editBankDetailResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    findNavController().popBackStack()
                    result = it.data
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

    }

    private fun initiateComponent() {
        val autotextView = binding.autoTvBankName
        val listBankName = resources.getStringArray(R.array.list_bank_name)
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_list_item_1, listBankName)
        autotextView.setAdapter(adapter)


    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}