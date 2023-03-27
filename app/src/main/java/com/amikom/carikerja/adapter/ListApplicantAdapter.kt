package com.amikom.carikerja.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutListApplicantBinding
import com.amikom.carikerja.models.Applicant
import com.amikom.carikerja.models.JobDetails
import com.amikom.carikerja.ui.bottom_nav.work.BottomSheetFragment
import com.squareup.picasso.Picasso

class ListApplicantAdapter(private var dataApplicant: List<Applicant>) : RecyclerView.Adapter<ListApplicantAdapter.ListApplicantViewHolder>() {

    inner class ListApplicantViewHolder(val binding: LayoutListApplicantBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListApplicantViewHolder {
        val binding = LayoutListApplicantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListApplicantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListApplicantViewHolder, position: Int) {
        val item = dataApplicant[position]

        holder.itemView.setOnClickListener{ view ->
            listenerBtnItem?.btnItem(item)
        }

        with(holder.binding){
            tvUserName.text = item.name
            if (item.address.isNullOrEmpty()){
                tvAddress.visibility = View.GONE
            } else {
                tvAddress.visibility = View.VISIBLE
                tvAddress.text = item.address
            }

            Picasso.get()
                .load("${item.imageUrl}")
                .error(R.drawable.dummy_avatar)
                .into(userImage)
        }


    }

    var listenerBtnItem: showBottomSheet? = null

    override fun getItemCount(): Int = dataApplicant.size

    fun setApplicantData(dataList: List<Applicant>) {
        this.dataApplicant = dataList
        notifyDataSetChanged()
    }

}

interface showBottomSheet {
    fun btnItem(data: Applicant)
}