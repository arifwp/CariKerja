package com.amikom.carikerja.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.databinding.LayoutWorkExperienceBinding
import com.amikom.carikerja.models.CertificateDetailString
import com.amikom.carikerja.models.Exp

class WorkExperienceAdapter(private var workExp: List<Exp>) : RecyclerView.Adapter<WorkExperienceAdapter.WorkExpViewHolder>() {

    inner class WorkExpViewHolder (val binding: LayoutWorkExperienceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkExpViewHolder {
        val binding = LayoutWorkExperienceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkExpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkExpViewHolder, position: Int) {
        val item = workExp[position]
        with(holder.binding){
            btnEdit.setOnClickListener {
                listenerWorkExp?.btnOnClickExp(item)
            }
            jobTitleWe.text = item.job_title
            companyWe.text = item.company
            employeeTypeWe.text = item.employee_type
            dateStart.text = item.dateStart
            dateEnd.text = item.dateEnd
            jobDescription.text = item.job_description
            jobAddress.text = item.job_address
        }
    }

    var listenerWorkExp: btnWorkExpClickClickListener? = null

    override fun getItemCount(): Int = workExp.size

    fun setWorkExpData(workExpList: List<Exp>) {
        this.workExp = workExpList
        notifyDataSetChanged()
    }

}

interface btnWorkExpClickClickListener {
    fun btnOnClickExp(data: Exp)
}