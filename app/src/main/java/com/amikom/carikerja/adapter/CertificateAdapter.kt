package com.amikom.carikerja.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutCertificateBinding
import com.amikom.carikerja.models.CertificateDetailString
import com.amikom.carikerja.models.CertificateDetails
import com.amikom.carikerja.models.EducationDetails
import com.squareup.picasso.Picasso

class CertificateAdapter(private var dataCertificate: List<CertificateDetailString>) : RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder>() {

    inner class CertificateViewHolder(val binding: LayoutCertificateBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        val binding = LayoutCertificateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CertificateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val item = dataCertificate[position]
        with(holder.binding){
            certificateName.text = item.title
            publishingOrganization.text = item.publishing_organization
            dateStart.text = item.dateStart
            dateEnd.text = item.expiration_date
            btnEdit.setOnClickListener {
                listener?.btnOnClick(item)
            }
            if (item.credential_id != ""){
                certificateId.text = item.credential_id
                wrapCredentialId.visibility = View.VISIBLE
            } else {
                wrapCredentialId.visibility = View.GONE
            }

            if (item.image.toString() == "null"){
                wrapImg.visibility = View.GONE
            } else if (item.image == null) {
                wrapImg.visibility = View.GONE
            } else {
                Picasso.get()
                    .load("${item.image}")
                    .error(R.drawable.dummy_avatar)
                    .into(holder.binding.imgCertificate)
                wrapImg.visibility = View.VISIBLE
            }
        }
    }

    var listener: btnClickClickListener? = null

    override fun getItemCount(): Int = dataCertificate.size

    fun setCertificateData(dataList: List<CertificateDetailString>) {
        this.dataCertificate = dataList
        notifyDataSetChanged()
    }
}

interface btnClickClickListener {
    fun btnOnClick(data: CertificateDetailString)
}