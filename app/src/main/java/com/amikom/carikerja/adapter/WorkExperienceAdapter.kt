package com.amikom.carikerja.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutWorkExperienceBinding
import com.amikom.carikerja.models.Exp


class WorkExperienceAdapter(private val page: String, private var workExp: List<Exp>) : RecyclerView.Adapter<WorkExperienceAdapter.WorkExpViewHolder>() {

    inner class WorkExpViewHolder (val binding: LayoutWorkExperienceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkExpViewHolder {
        val binding = LayoutWorkExperienceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkExpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkExpViewHolder, position: Int) {
        val item = workExp[position]
        Log.d("WorkExperienceAdapter", "page : $page")

        when {
            page == "WorkExperienceFragment" -> {
                with(holder.binding){
                    btnEdit.setOnClickListener {
                        listenerWorkExp?.btnOnClickExp(item)
                    }
                    jobTitleWe.text = item.job_title
                    companyWe.text = item.company
                    employeeTypeWe.text = item.employee_type
                    dateStart.text = item.dateStart
                    dateEnd.text = item.dateEnd
                    jobAddress.text = item.job_address

                    jobDescription.text = item.job_description
                    jobDescription.setOnClickListener {
                        jobDescription.toggle()
                    }
                }
            }

            page == "BottomSheetFragment" -> {
                with(holder.binding){
                    btnEdit.visibility = View.GONE
                    jobTitleWe.text = item.job_title
                    companyWe.text = item.company
                    employeeTypeWe.text = item.employee_type
                    dateStart.text = item.dateStart
                    dateEnd.text = item.dateEnd
                    jobAddress.text = item.job_address

                    jobDescription.text = item.job_description
//                    jobDescription.setOnClickListener {
//                        jobDescription.toggle()
//                        if (jobDescription.isExpanded())
//                            jobDescription.toggle()
//
//                    }
//                    jobDescription
//                        .setAnimationDuration(500)
//                        .setReadMoreText("Lebih Banyak")
//                        .setReadLessText("Lebih Sedikit")
//                        .setCollapsedLines(3)
//                        .setIsExpanded(true)
//                        .setIsUnderlined(true)
//                        .setEllipsizedTextColor(ContextCompat.getColor(context, R.color.purple_200))
                }
            }
        }

    }

    var listenerWorkExp: btnWorkExpClickClickListener? = null

    override fun getItemCount(): Int = workExp.size

    fun setWorkExpData(workExpList: List<Exp>?) {
        if (workExpList != null) {
            this.workExp = workExpList
        }
        notifyDataSetChanged()
    }

}

interface btnWorkExpClickClickListener {
    fun btnOnClickExp(data: Exp)
}