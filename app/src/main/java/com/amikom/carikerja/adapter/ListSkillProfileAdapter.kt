package com.amikom.carikerja.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutSkillsBinding
import com.amikom.carikerja.models.SkillsDetail

class ListSkillProfileAdapter(private var dataSkills: List<SkillsDetail>) : RecyclerView.Adapter<ListSkillProfileAdapter.ListSkillProfileViewHolder>() {

    var selectedList: ArrayList<SkillsDetail> = ArrayList()
    var selectedName: String? = null
    private val TAG = "ListSkillProfileAdapter"

    inner class ListSkillProfileViewHolder(val binding: LayoutSkillsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSkillProfileViewHolder {
        val binding = LayoutSkillsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListSkillProfileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListSkillProfileViewHolder, position: Int) {
        val item = dataSkills[position]

        with(holder.binding){
            skillsItem.setText(item.skill_name)
            skillsItem.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    skillsItem.setBackgroundResource(R.drawable.bg_choose_skills_active)
                    skillsItem.setTextColor(Color.parseColor("#FFFFFF"))
                    selectedList.add(SkillsDetail(
                        id = item.id,
                        skill_name = item.skill_name.toString()
                    ))
                    selectedName = item.skill_name.toString()
                    Log.d(TAG, "add: $selectedList")
                    notifyDataSetChanged()
                } else if (!isChecked){
                    selectedList.remove(
                        SkillsDetail(
                            id = item.id,
                            skill_name = item.skill_name.toString()
                        )
                    )
                    selectedName = null
                    Log.d(TAG, "remove: $selectedList")
                    notifyDataSetChanged()
                    skillsItem.setTextColor(Color.parseColor("#2A2A2A"))
                    skillsItem.setBackgroundResource(R.drawable.bg_choose_skills_unactive)
                }
                listenerDeleteSkills?.btnOnDeleteSkills(selectedList, selectedName)
                notifyDataSetChanged()
            }

            skillsItem.tag = position
        }

    }

    fun getSelected(): ArrayList<SkillsDetail> {
        return selectedList
    }

    var listenerDeleteSkills: chooseDeleteSkillsListener? = null

    override fun getItemCount(): Int = dataSkills.size

    fun setSkillsData(dataList: List<SkillsDetail>) {
        this.dataSkills = dataList
        notifyDataSetChanged()
    }

}

interface chooseDeleteSkillsListener {
    fun btnOnDeleteSkills(data: ArrayList<SkillsDetail>, selectedName: String?)
}