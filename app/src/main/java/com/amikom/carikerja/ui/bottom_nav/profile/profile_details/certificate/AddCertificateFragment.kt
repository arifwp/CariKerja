package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.certificate

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentAddCertificateBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.models.CertificateDetails
import com.amikom.carikerja.utils.SharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class AddCertificateFragment : Fragment() {

    private var _binding: FragmentAddCertificateBinding? = null
    private val binding get() = _binding!!
    private val certificateViewModel: CertificateViewModel by viewModels()
    private var TAG = "AddCertificateFragment"
    private var selectedImage: Uri? = null
    private var dateFormater: String? = null
    private val args: AddCertificateFragmentArgs by navArgs()
    private var uid: String? = null
    private var id: String? = null
    private var certificate_name: String? = null
    private var publishing_organization: String? = null
    private var issueDate: String? = null
    private var expiration_data: String? = null
    private var credential_id: String? = null
    private var credential_url: String? = null
    private var image: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddCertificateBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe()
        initiateComponent()
        uid = SharedPreferences.getUid(requireContext())

        selectedImage = null
        dateFormater = SimpleDateFormat("MMMM yyyy").format(Calendar.getInstance().time)


        val btnSubmit = binding.btnSubmit
        id = args.id
        if (id != null){
            observeDataEdit()
        } else {
            btnSubmit.setOnClickListener {
                validateData(uid.toString())
            }
        }

        val btnAddCertificateImage = binding.wrapAddMedia
        btnAddCertificateImage.setOnClickListener {
            showDialog()
        }

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeDataEdit() {
        certificate_name = args.certificateName
        publishing_organization = args.publishingOrganization
        issueDate = args.dateStart
        expiration_data = args.expirationDate
        credential_id = args.credentialId
        credential_url = args.credentialUrl
        image = args.imageUrl

        val certificateName = binding.edCertificateName
        val publish = binding.edPublishingOrganization
        val dateStart = binding.edDateStart
        val dateEnd = binding.edDateEnd
        val certificateId = binding.edCertificateId
        val certificateUrl = binding.edCertificateUrl

        certificateName.setText(certificate_name)
        publish.setText(publishing_organization)
        dateStart.setText(issueDate)
        dateEnd.setText(expiration_data)
        certificateId.setText(credential_id)
        certificateUrl.setText(credential_url)


        if (image == null){
            binding.imgPreview.visibility = View.GONE
        } else if (image == "null"){
            binding.imgPreview.visibility = View.GONE
        }else{
            Picasso.get()
                .load("$image")
                .error(R.drawable.dummy_avatar)
                .into(binding.imgPreview)
            binding.imgPreview.visibility = View.VISIBLE
        }

        binding.deleteData.visibility = View.VISIBLE

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            certificateViewModel.editCertificate(
                uid.toString(),
                id.toString(),
                certificateName.text.toString().trim(),
                publish.text.toString().trim(),
                dateStart.text.toString().trim(),
                dateEnd.text.toString().trim(),
                certificateId.text.toString().trim(),
                certificateUrl.text.toString().trim(),
                image.toString(),
                selectedImage
            )
        }

        val btnDelete = binding.deleteData
        btnDelete.setOnClickListener {
            certificateViewModel.deleteCertificate(uid.toString(), id.toString(), image.toString())
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                selectedImage = data.data!!

                binding.imgPreview.setImageURI(selectedImage)
                binding.imgPreview.visibility = View.VISIBLE
            } else {
                binding.imgPreview.visibility = View.GONE
            }
        } else {
            binding.imgPreview.visibility = View.GONE
        }
    }

    private fun observe() {
        certificateViewModel.addCertificateResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data)
                }
                is BaseResponse.Error -> {
                    textMessage(it.msg.toString())
                }
            }
        }

        certificateViewModel.editCertificateResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data)
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }

        certificateViewModel.deleteCertificateResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data.toString())
                    findNavController().popBackStack()
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    private fun validateData(uid: String) {
        val certificateName = binding.edCertificateName.text.toString().trim()
        val publishingOrganization = binding.edPublishingOrganization.text.toString().trim()
        val issueDate = binding.edDateStart.text.toString().trim()
        val expirationDate = binding.edDateEnd.text.toString().trim()
        val certificateId = binding.edCertificateId.text.toString().trim()
        val certificateUrl = binding.edCertificateUrl.text.toString().trim()


        val certificateDetails = CertificateDetails(
            title = certificateName,
            publishing_organization = publishingOrganization,
            dateStart = issueDate,
            expiration_date = expirationDate,
            credential_id = certificateId,
            credential_url = certificateUrl,
            image = selectedImage
        )

        when {
            certificateName.isEmpty() -> {
                binding.edCertificateName.error = "Masukkan nama sertifikat"
            }
            issueDate.isEmpty() -> {
                binding.edDateStart.error = "Masukkan tanggal publikasi sertifikat"
            }
            expirationDate.isEmpty() -> {
                binding.edDateEnd.error = "Masukkan tanggal jatuh tempo sertifikat"
            }
            binding.imgPreview.visibility == View.GONE -> {
                textMessage("Masukkan foto sertifikat")
            }
            else -> {
                certificateViewModel.addCertificate(uid.toString(), certificateDetails)
            }
        }


    }

    private fun showDialog() {
//        val dialog = BottomSheetDialog(requireContext())
//
//        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_media, null)
//        val btnGallery = bottomSheet.findViewById<LinearLayout>(R.id.wrap_gallery)
//
//        btnGallery.setOnClickListener{
//            dialog.dismiss()
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
//        }
//
//        dialog.setCancelable(true)
//        dialog.setContentView(bottomSheet)
//
//        dialog.show()
    }

    private fun initiateComponent() {
        binding.edDateStart.setOnClickListener {

            var c = Calendar.getInstance()
            var cDay = c.get(Calendar.DAY_OF_MONTH)
            var cMonth = c.get(Calendar.MONTH)
            var cYear = c.get(Calendar.YEAR)

            val calenderDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    cDay = dayOfMonth
                    cMonth = month
                    cYear = year
                    val dateStart = binding.edDateStart
                    dateStart.setText("$cDay-${cMonth+1}-$cYear")

                }, cYear, cMonth, cDay)
            calenderDialog.show()
        }

        binding.edDateEnd.setOnClickListener {

            var c = Calendar.getInstance()
            var cDay = c.get(Calendar.DAY_OF_MONTH)
            var cMonth = c.get(Calendar.MONTH)
            var cYear = c.get(Calendar.YEAR)

            val calenderDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    cDay = dayOfMonth
                    cMonth = month
                    cYear = year
                    val dateEnd = binding.edDateEnd
                    dateEnd.setText("$cDay-${cMonth+1}-$cYear")

                }, cYear, cMonth, cDay)
            calenderDialog.show()
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}