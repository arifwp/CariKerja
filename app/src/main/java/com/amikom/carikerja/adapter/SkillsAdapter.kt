package com.amikom.carikerja.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutSkillsBinding
import com.amikom.carikerja.models.JobCategory
import com.amikom.carikerja.models.SkillsDetail

class SkillsAdapter(private var dataJobCategory: List<JobCategory>) : RecyclerView.Adapter<SkillsAdapter.SkillsViewHolder>() {

//    private lateinit var dataSkills: Array<String>
    var selectedList: ArrayList<SkillsDetail> = ArrayList()

    inner class SkillsViewHolder(val binding: LayoutSkillsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        val binding = LayoutSkillsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        val item = dataJobCategory[position]
        with(holder.binding){
            skillsItem.text = item.name.toString()
            skillsItem.setOnCheckedChangeListener{ buttonView, isCheckd ->
                if (isCheckd){
                    skillsItem.setBackgroundResource(R.drawable.bg_choose_skills_active)
                    skillsItem.setTextColor(Color.parseColor("#FFFFFF"))
                    selectedList.add(SkillsDetail(
                        id = item.id,
                        skill_name = item.name.toString()
                    ))
                    notifyDataSetChanged()
                } else if (!isCheckd){
                    selectedList.remove(
                        SkillsDetail(
                        id = item.id,
                        skill_name = item.name.toString()
                    )
                    )
                    skillsItem.setTextColor(Color.parseColor("#2A2A2A"))
                    notifyDataSetChanged()
                    skillsItem.setBackgroundResource(R.drawable.bg_choose_skills_unactive)
                }
                listenerChooseSkills?.btnOnClickChooseSkills(selectedList, position)
                notifyDataSetChanged()
            }
            skillsItem.setTag(position)
        }
    }

    fun getSelected(): ArrayList<SkillsDetail> {
        return selectedList
    }

    var listenerChooseSkills: chooseSkillsListener? = null

    override fun getItemCount(): Int = dataJobCategory.size

    fun setDataSkills(dataList: List<JobCategory>) {
        this.dataJobCategory = dataList
        notifyDataSetChanged()
    }

}

interface chooseSkillsListener {
    fun btnOnClickChooseSkills(data: ArrayList<SkillsDetail>, position: Int)
}