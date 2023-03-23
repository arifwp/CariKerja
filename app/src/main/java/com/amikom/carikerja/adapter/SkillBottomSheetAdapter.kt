package com.amikom.carikerja.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.databinding.LayoutRvSkillsBinding
import com.amikom.carikerja.models.SkillsDetail

class SkillBottomSheetAdapter(private var skill: List<SkillsDetail>) : RecyclerView.Adapter<SkillBottomSheetAdapter.SkillViewHolder>() {

    inner class SkillViewHolder(val binding: LayoutRvSkillsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val binding = LayoutRvSkillsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SkillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val item = skill[position]
        Log.d("SkillBottomAdapter", "onBindViewHolder: $item")
        with(holder.binding){
            tvSkills.text = item.skill_name
        }
    }

    override fun getItemCount(): Int = skill.size

    fun setSkillData(skillList: List<SkillsDetail>){
        this.skill = skillList
        notifyDataSetChanged()
    }

}