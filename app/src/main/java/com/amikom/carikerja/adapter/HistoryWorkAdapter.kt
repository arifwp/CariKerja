package com.amikom.carikerja.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutHistoryWorkBinding
import com.amikom.carikerja.models.HistoryJob

class HistoryWorkAdapter(private val context: Context, private var dataHistory: List<HistoryJob>) : RecyclerView.Adapter<HistoryWorkAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: LayoutHistoryWorkBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = LayoutHistoryWorkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = dataHistory[position]

        holder.itemView.setOnClickListener {
            listenerBtnHistory?.btnClickItem(item.id_job)
        }

        with(holder.binding){
            tvJobTitle.text = item.job_title
            tvRecruiterName.text = item.recruiter_name
            if (item.status == "rejected"){
                wrapApplicantStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red_100);
                tvApplicantStatus.setTextColor(ContextCompat.getColor(context, R.color.red_400))
                tvApplicantStatus.text = "Ditolak"
            } else if (item.status == "accepted"){
                wrapApplicantStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green_100);
                tvApplicantStatus.setTextColor(ContextCompat.getColor(context, R.color.green_700))
                tvApplicantStatus.text = "Diterima"
            } else if (item.status == "on_review"){
                wrapApplicantStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.yellow_100);
                tvApplicantStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow_700))
                tvApplicantStatus.text = "Sedang ditinjau"
            }
        }
    }

    var listenerBtnHistory: btnClickHistoryWork? = null

    override fun getItemCount(): Int = dataHistory.size

    fun setHistoryWorkData(dataList: List<HistoryJob>){
        this.dataHistory = dataList
        notifyDataSetChanged()
    }

}

interface btnClickHistoryWork {
    fun btnClickItem(idJob: String?)
}