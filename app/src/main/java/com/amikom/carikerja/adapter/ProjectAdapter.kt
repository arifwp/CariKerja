package com.amikom.carikerja.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.databinding.LayoutProjectBinding
import com.amikom.carikerja.models.EducationDetails
import com.amikom.carikerja.models.Exp
import com.amikom.carikerja.models.ProjectDetails

class ProjectAdapter(private var dataProject: List<ProjectDetails>) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(val binding: LayoutProjectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val binding = LayoutProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val item = dataProject[position]
        with(holder.binding){
            projectName.text = item.project_name
            role.text = item.role
            dateStart.text = item.dateStart
            dateEnd.text = item.dateEnd
            jobDescription.text = item.description
            btnEdit.setOnClickListener {
                listenerEditProject?.btnOnClickEdit(item)
            }
        }
    }

    var listenerEditProject: btnEditProjectListener? = null

    override fun getItemCount(): Int = dataProject.size

    fun setProjectData(dataList: List<ProjectDetails>) {
        this.dataProject = dataList
        notifyDataSetChanged()
    }

}

interface btnEditProjectListener {
    fun btnOnClickEdit(data: ProjectDetails)
}