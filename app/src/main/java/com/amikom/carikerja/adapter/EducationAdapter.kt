package com.amikom.carikerja.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.databinding.LayoutEducationBinding
import com.amikom.carikerja.models.EducationDetails
import com.amikom.carikerja.models.Exp

class EducationAdapter(private var dataEdu: List<EducationDetails>) : RecyclerView.Adapter<EducationAdapter.EducationViewHolder>(){

    inner class EducationViewHolder(val binding: LayoutEducationBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EducationViewHolder {
        val binding = LayoutEducationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EducationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EducationViewHolder, position: Int) {
        val item = dataEdu[position]
        with(holder.binding){
            institution.text = item.institution
            degree.text = item.degree
            fieldOfStudy.text = item.field_of_study
            dateStart.text = item.dateStart
            dateEnd.text = item.dateEnd
            jobDescription.text = item.description
            btnEdit.setOnClickListener {
                listenerEducationEdit?.btnOnClickEducation(item)
            }
        }
    }

    var listenerEducationEdit: btnEducationEdit? = null

    override fun getItemCount(): Int = dataEdu.size

    fun setEducationData(dataList: List<EducationDetails>) {
        this.dataEdu = dataList
        notifyDataSetChanged()
    }


}

interface btnEducationEdit {
    fun btnOnClickEducation(data: EducationDetails)
}