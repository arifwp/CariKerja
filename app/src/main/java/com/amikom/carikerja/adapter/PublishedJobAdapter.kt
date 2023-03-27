package com.amikom.carikerja.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutJobBinding
import com.amikom.carikerja.models.JobDetails
import com.amikom.carikerja.ui.bottom_nav.history_work.HistoryPostJobFragment
import com.amikom.carikerja.ui.bottom_nav.history_work.HistoryPostJobFragmentDirections
import com.amikom.carikerja.utils.TimeShow
import com.squareup.picasso.Picasso

class PublishedJobAdapter(private var dataJob: List<JobDetails>) : RecyclerView.Adapter<PublishedJobAdapter.PublishedJobViewHolder>() {

    inner class PublishedJobViewHolder(val binding: LayoutJobBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublishedJobViewHolder {
        val binding = LayoutJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PublishedJobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PublishedJobViewHolder, position: Int) {
        val item = dataJob[position]

        // Time Ago
        val timeDetail: String = item.post_time.toString()
        val timeAgo2Detail = TimeShow()
        val myFinalValueDetail: String = timeAgo2Detail.covertTimeToText(timeDetail).toString()

        holder.itemView.setOnClickListener{ view ->
            view.findNavController().navigate(HistoryPostJobFragmentDirections.actionNavigationHistoryPostJobWorkToDetailJobFragment(
                item.id,
                item.uid,
                item.job_title,
                item.person_who_post,
                item.image_url,
                item.dateStart,
                item.dateEnd,
                item.total_day,
                item.job_description,
                item.job_category,
                item.employee_type,
                item.job_address,
                item.salary,
                myFinalValueDetail
            ))
        }

        with(holder.binding){
            jobTitle.text = item.job_title
            userName.text = item.person_who_post
            workDuration.text = item.total_day
            workLocation.text = item.job_address
            workSalary.text = item.salary

            // Time Ago
            val time: String = item.post_time.toString()
            val timeAgo2 = TimeShow()
            val myFinalValue: String = timeAgo2.covertTimeToText(time).toString()
            timePost.text = myFinalValue

            // Applied Counter
            jobApplied.text = 12.toString()

            if (item.salary.isNullOrBlank()){
                wrapSalary.visibility = View.GONE
            } else {
                wrapSalary.visibility = View.VISIBLE
                workSalary.text = item.salary
            }

            if (item.employee_type.isNullOrBlank()){
                wrapJobType.visibility = View.GONE
            } else {
                wrapJobType.visibility = View.VISIBLE
                jobType.text = item.employee_type
            }

            Picasso.get()
                .load("${item.image_url}")
                .error(R.drawable.dummy_avatar)
                .into(holder.binding.userImage)
        }

    }

    override fun getItemCount(): Int = dataJob.size

    fun setPublishedJobData(dataList: List<JobDetails>) {
        this.dataJob = dataList
        notifyDataSetChanged()
    }

}