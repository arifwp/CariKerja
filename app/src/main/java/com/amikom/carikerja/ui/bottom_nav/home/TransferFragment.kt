package com.amikom.carikerja.ui.bottom_nav.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.amikom.carikerja.databinding.FragmentTransferBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SdkUiMidtrans
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel : ProfileViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var TAG = "TransferFragment"
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
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uid = SharedPreferences.getUid(requireContext())
        observe()
        profileViewModel.getProfile(uid.toString())

        SdkUiMidtrans.initSdkUiFlow(requireContext())

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {

            homeViewModel.getInvoiceStatus("b265312b-88f1-4856-b152-24eff2cd2df9")

//            val harga = 250000
//            val total = 250000
//
//            val transactionRequest = TransactionRequest("CK-"+System.currentTimeMillis().toShort(), total.toDouble())
//            val detail = ItemDetails(
//                "JenisTranksasiId",
//                harga.toDouble(),
//                1,
//                "Pembayaran top-up saldo"
//            )
//
//            val itemDetails = ArrayList<ItemDetails>()
//            itemDetails.add(detail)
//
//            uiKitDetails(transactionRequest)
//            transactionRequest.itemDetails = itemDetails
//
//            MidtransSDK.getInstance().transactionRequest = transactionRequest
//            MidtransSDK.getInstance().startPaymentUiFlow(requireContext())
        }
    }

    private fun observe(){

        homeViewModel.getInvoiceStatusResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data?.data?.status.toString())
                }
                is BaseResponse.Error -> {
                    textMessage(it.msg.toString())
                }
                else -> {}
            }
        }

//        profileViewModel.getProfileResponse.observe(viewLifecycleOwner){
//            when(it){
//                is BaseResponse.Loading -> {}
//                is BaseResponse.Success -> {
//                    name = it.data.name
//                    email = it.data.email
//                    phone = it.data.phone
//                    address = it.data.address
//                }
//                is BaseResponse.Error -> {
//                    textMessage(it.msg.toString())
//                }
//            }
//        }
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