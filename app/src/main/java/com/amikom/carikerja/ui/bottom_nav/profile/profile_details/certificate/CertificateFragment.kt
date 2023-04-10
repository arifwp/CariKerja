package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.certificate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.amikom.carikerja.adapter.CertificateAdapter
import com.amikom.carikerja.adapter.btnClickClickListener
import com.amikom.carikerja.databinding.FragmentCertificateBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.CertificateDetailString
import com.amikom.carikerja.utils.SharedPreferences
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CertificateFragment : Fragment(), btnClickClickListener {

    private var _binding: FragmentCertificateBinding? = null
    private val binding get() = _binding!!
    private val certificateViewModel: CertificateViewModel by viewModels()
    private lateinit var certificateAdapter: CertificateAdapter
    private var TAG = "CertificateFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCertificateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateRv()
        val uid = SharedPreferences.getUid(requireContext())
        certificateViewModel.getCertificate(uid.toString())

        val btnAddCertificate = binding.btnSubmit
        btnAddCertificate.setOnClickListener {
            findNavController()
                .navigate(
                    CertificateFragmentDirections.actionCertificateFragmentToAddCertificateFragment(
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

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun btnOnClick(data: CertificateDetailString) {
        findNavController()
            .navigate(
                CertificateFragmentDirections.actionCertificateFragmentToAddCertificateFragment(
                    data.id,
                    data.title,
                    data.publishing_organization,
                    data.dateStart,
                    data.expiration_date,
                    data.credential_id,
                    data.credential_url,
                    data.image
                ))
    }

    override fun btnSeeUrl(url: String) {
        if (!url.startsWith("http://") && !url.startsWith("https://")){
            val data = "http://" + url;
            val uri: Uri = Uri.parse(data) // missing 'http://' will cause crashed

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } else {
            textMessage("Tidak bisa membuka link url")
        }
    }

    private fun observe() {
        certificateViewModel.getCertificateResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    certificateAdapter.setCertificateData(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun initiateRv() {
        val recyclerViewCertificate: RecyclerView = requireView().findViewById(R.id.rv_certificate)
        certificateAdapter = CertificateAdapter("CertificateFragment", ArrayList())
        certificateAdapter.listener = this
        recyclerViewCertificate.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = certificateAdapter
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}