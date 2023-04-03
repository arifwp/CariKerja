package com.amikom.carikerja.ui.bottom_nav.profile.profile_details.biodata

import android.app.AlertDialog
import android.app.DatePickerDialog
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
import androidx.navigation.fragment.navArgs
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.FragmentBiodataBinding
import com.amikom.carikerja.models.BaseResponse
import com.amikom.carikerja.utils.SharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class BiodataFragment : Fragment() {

    private var _binding: FragmentBiodataBinding? = null
    private val binding get() = _binding!!
    private val biodataViewModel: BiodataViewModel by viewModels()
    private var selectedImage: Uri? = null
    private lateinit var alertDialog: AlertDialog.Builder
    private val args: BiodataFragmentArgs by navArgs()

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

        initiateComponent()
        selectedImage = null

        val btnBack = binding.icBack
        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        setData()

        alertDialog = AlertDialog.Builder(requireContext())
            .setMessage("Update")
            .setCancelable(false)



        val btnUploadImage = binding.userImage
        btnUploadImage?.setOnClickListener {

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
            uploadData()
        }


    }

    private fun initiateComponent() {
        binding.edBod.setOnClickListener {
            var c = Calendar.getInstance()
            var cDay = c.get(Calendar.DAY_OF_MONTH)
            var cMonth = c.get(Calendar.MONTH)
            var cYear = c.get(Calendar.YEAR)

            val calenderDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    cDay = dayOfMonth
                    cMonth = month
                    cYear = year
                    val dateEnd = binding.edBod
                    dateEnd.setText("$cDay-${cMonth+1}-$cYear")

                }, cYear, cMonth, cDay)
            calenderDialog.show()
        }
    }

    private fun setData() {
        binding.edFullName.setText(args.name)

        if (args.address == "null"){
            binding.edAddress.text = null
        } else {
            binding.edAddress.setText(args.address)
        }

        if (args.dob == "null"){
            binding.edBod.text = null
        } else {
            binding.edBod.setText(args.dob)
        }

        if (args.imageUrl == "null"){
            binding.userImage?.setBackgroundResource(R.drawable.dummy_avatar)
        } else {
            Picasso.get()
                .load(args.imageUrl)
                .error(R.drawable.dummy_avatar)
                .into(binding.userImage)
        }
    }

    private fun uploadData() {

        val uid = SharedPreferences.getUid(requireContext())
        val name = binding.edFullName.text.toString().trim()
        val dob = binding.edBod.text.toString().trim()
        val address = binding.edAddress?.text.toString().trim()

        biodataViewModel.editProfile(uid = uid.toString(), selectedImage, name, dob, address, args.name)
        biodataViewModel.editProfileResponse.observe(viewLifecycleOwner){
            it.getContentIfNotHandled().let {
                when(it){
                    is BaseResponse.Loading -> {}
                    is BaseResponse.Success -> {
                        textMessage(it.data.toString())
                    }
                    is BaseResponse.Error -> textMessage(it.msg.toString())
                    else -> {}
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                selectedImage = data.data!!

                binding.userImage?.setImageURI(selectedImage)
            }
        }
    }

    private fun textMessage(message: String) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

}