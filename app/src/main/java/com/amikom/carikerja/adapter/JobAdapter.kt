package com.amikom.carikerja.adapter

import a.a.a.a.a.i
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.amikom.carikerja.R
import com.amikom.carikerja.databinding.LayoutJobBinding
import com.amikom.carikerja.models.JobDetails
import com.amikom.carikerja.ui.bottom_nav.work.WorkFragmentDirections
import com.amikom.carikerja.utils.TimeShow
import com.squareup.picasso.Picasso
import java.util.*


class JobAdapter(private var dataJob: List<JobDetails>) : RecyclerView.Adapter<JobAdapter.JobViewHolder>(){

    private val TAG = "JobAdapter"

    inner class JobViewHolder(val binding : LayoutJobBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = LayoutJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val item = dataJob[position]

        // Time Ago
        val timeDetail: String = item.post_time.toString()
        val timeAgo2Detail = TimeShow()
        val myFinalValueDetail: String = timeAgo2Detail.covertTimeToText(timeDetail).toString()

        holder.itemView.setOnClickListener { view ->
            view.findNavController().navigate(WorkFragmentDirections.actionNavigationWorkToDetailJobFragment(
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
//            jobApplied.text = 12.toString()

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

    fun filterCars(cars: List<JobDetails>, searchText: String): List<JobDetails>? {
        val filteredCars: MutableList<JobDetails> = ArrayList()
        var c: JobDetails
        for (i in cars.indices) {
            c = cars[i]
            if (c.job_title?.lowercase()
                    !!.contains(searchText.trim { it <= ' ' }.lowercase(Locale.getDefault()))
            ) {
                filteredCars.add(c)
            }
        }
        return filteredCars
    }

    fun filterJobCategory(cars: List<JobDetails>?, searchText: String?): List<JobDetails>? {
        val filteredCars: MutableList<JobDetails> = ArrayList()
        var c: JobDetails
        if (cars != null) {
            for (i in cars.indices) {
                c = cars[i]
                if (c.job_category!!.contains(searchText.toString())) {
                    Log.d(TAG, "filterJobCategory: $searchText")
                    filteredCars.add(c)
                }
            }
        }
        return filteredCars
    }

    fun setJobData(dataList: List<JobDetails>) {
        this.dataJob = dataList
        notifyDataSetChanged()
    }
}