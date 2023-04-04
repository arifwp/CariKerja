package com.amikom.carikerja.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutSkillsBinding
import com.amikom.carikerja.models.JobCategory
import com.amikom.carikerja.models.SkillsDetail

class AddSkillsProfileAdapter(private var dataAddSkills: List<JobCategory>) : RecyclerView.Adapter<AddSkillsProfileAdapter.AddSkillsViewHolder>() {

    var selectedList: ArrayList<SkillsDetail> = ArrayList()

    inner class AddSkillsViewHolder(val binding: LayoutSkillsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddSkillsViewHolder {
        val binding = LayoutSkillsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddSkillsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddSkillsViewHolder, position: Int) {
        val item = dataAddSkills[position]
        with(holder.binding){
            skillsItem.text = item.name.toString()

            skillsItem.setOnCheckedChangeListener{ buttonView, isCheckd ->
                if (isCheckd){
                    skillsItem.setBackgroundResource(R.drawable.bg_choose_skills_active)
                    skillsItem.setTextColor(Color.parseColor("#FFFFFF"))
                    selectedList.add(
                        SkillsDetail(
                        id = item.id,
                        skill_name = item.name.toString()
                    )
                    )
                } else if (!isCheckd){
                    selectedList.remove(
                        SkillsDetail(
                            id = item.id,
                            skill_name = item.name.toString()
                        )
                    )
                    skillsItem.setTextColor(Color.parseColor("#2A2A2A"))
                    skillsItem.setBackgroundResource(R.drawable.bg_choose_skills_unactive)
                }
                listenerAddSkillsListener?.btnAddSkills(selectedList)
            }


            skillsItem.tag = position
        }
    }

    var listenerAddSkillsListener: btnAddSkillsListener? = null

    override fun getItemCount(): Int = dataAddSkills.size

    fun setAddDataSkills(dataList: List<JobCategory>) {
        this.dataAddSkills = dataList
        notifyDataSetChanged()
    }

}

interface btnAddSkillsListener {
    fun btnAddSkills(data : ArrayList<SkillsDetail>)
}