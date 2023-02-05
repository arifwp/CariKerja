package com.amikom.carikerja.ui.profile.profile_details.biodata

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentBiodataBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.amikom.carikerja.viewmodels.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BiodataFragment : Fragment() {

    private var _binding: FragmentBiodataBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var database2: DatabaseReference
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImage: Uri
    private lateinit var alertDialog: AlertDialog.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBiodataBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        alertDialog = AlertDialog.Builder(requireContext())
            .setMessage("Update")
            .setCancelable(false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()



        val btnUploadImage = binding.ivUploadImage
        btnUploadImage.setOnClickListener {

            val dialog = BottomSheetDialog(requireContext())

            val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet_media, null)
            val btnGallery = bottomSheet.findViewById<LinearLayout>(R.id.wrap_gallery)

            btnGallery.setOnClickListener{
                dialog.dismiss()
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }

            dialog.setCancelable(true)
            dialog.setContentView(bottomSheet)

            dialog.show()
        }

        val btnSubmit = binding.btnSubmit
        btnSubmit.setOnClickListener {
            if (selectedImage == null) {
                textMessage("Upload foto anda")
            } else {
                uploadData()
            }
        }


    }

    private fun uploadData() {

        val uid = SharedPreferences.getUid(requireContext())
        profileViewModel.editProfile(uid = uid.toString(), selectedImage)
        profileViewModel.editProfileResponse.observe(viewLifecycleOwner){
            when(it){
                is BaseResponse.Loading -> {}
                is BaseResponse.Success -> {
                    textMessage(it.data.toString())
                }
                is BaseResponse.Error -> textMessage(it.msg.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                selectedImage = data.data!!

                binding.ivUploadImage.setImageURI(selectedImage)
            }
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}