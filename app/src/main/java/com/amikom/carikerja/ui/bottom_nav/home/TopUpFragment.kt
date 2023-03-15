package com.amikom.carikerja.ui.bottom_nav.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentTopUpBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SdkUiMidtrans
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TopUpFragment : Fragment() {

    private var _binding: FragmentTopUpBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private var TAG = "TopUpFragment"
    private var amount: String? = null
    private var uid: String? = null
    private var name: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var address: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopUpBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        listener(view)
        uid = SharedPreferences.getUid(requireContext())
        profileViewModel.getProfile(uid.toString())
        SdkUiMidtrans.initSdkUiFlow(requireContext())

    }

    private fun observe() {
        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it) {
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        name = it.data.name
                        email = it.data.email
                        phone = it.data.phone
                        address = it.data.address
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }
    }

    private fun listener(view: View) {
        binding.amount20000.setOnClickListener {
            binding.amount20000.setBackgroundResource(R.drawable.bg_amount_filled)
            binding.amount20000.setTextColor(resources.getColor(R.color.white))
            binding.edBalance.setText("20000")
        }

        binding.amount50000.setOnClickListener {
            binding.amount50000.setBackgroundResource(R.drawable.bg_amount_filled)
            binding.amount50000.setTextColor(resources.getColor(R.color.white))
            binding.edBalance.setText("50000")
        }

        binding.amount100000.setOnClickListener {
            binding.amount100000.setBackgroundResource(R.drawable.bg_amount_filled)
            binding.amount100000.setTextColor(resources.getColor(R.color.white))
            binding.edBalance.setText("100000")
        }

        binding.btnContinue.setOnClickListener {
            if (binding.edBalance.text.toString().isNullOrEmpty()){
                textMessage("Masukkan jumlah saldo yang akan diisi")
            } else {
                showBottomSheet()
            }
        }

    }

    private fun showBottomSheet() {
        val balance = binding.edBalance.text.toString().trim()
        Log.d(TAG, "showBottomSheet: $balance")
        Log.d(TAG, "showBottomSheetEmail: $email")
        Log.d(TAG, "showBottomSheetPhone: $phone")
        Log.d(TAG, "showBottomSheetName: $name")
        Log.d(TAG, "showBottomSheetAdress: $address")
        val dialog = BottomSheetDialog(requireContext())

        val bottomSheet = layoutInflater.inflate(R.layout.bottomsheet_payment_summary, null)
        val btnTopUp = bottomSheet.findViewById<MaterialButton>(R.id.btn_top_up)

        btnTopUp.setOnClickListener{
            dialog.dismiss()
            if (balance?.isNullOrBlank() == true){
                textMessage("Masukkan jumlah saldo yang akan diisikan")
            } else {
                initiatePayment(balance)
            }

        }

        dialog.setCancelable(true)
        dialog.setContentView(bottomSheet)

        dialog.show()
    }

    private fun initiatePayment(balance: String) {
        val harga = balance
        val total = balance

        val transactionRequest = TransactionRequest("CK-"+System.currentTimeMillis().toShort(), total.toDouble())
        val detail = ItemDetails(
            "JenisTranksasiId",
            harga.toDouble(),
            1,
            "Pembayaran top-up saldo"
        )

        val itemDetails = ArrayList<ItemDetails>()
        itemDetails.add(detail)

        uiKitDetails(transactionRequest)
        transactionRequest.itemDetails = itemDetails

        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(requireContext())
    }

    private fun uiKitDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = name
        customerDetails.phone = phone
        customerDetails.firstName = name
//        customerDetails.lastName = "Utomo"
        customerDetails.email = email
        val shippingAddress = ShippingAddress()
        shippingAddress.address = null
        shippingAddress.city = null
        shippingAddress.postalCode = null
        customerDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address = null
        billingAddress.city = null
        billingAddress.postalCode = null
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}