package com.amikom.carikerja.models

data class Job(
        var id: String? = null,
        var uid: String? = null,
        var job_title: String? = null,
        var person_who_post: String? = null,
        var image_url: String? = null,
        var dateStart: String? = null,
        var dateEnd: String? = null,
        var total_day: String? = null,
        var job_description: String? = null,
        var job_category: String? = null,
        var employee_type: String? = null,
        var job_address: String? = null,
        var salary: String? = null,
        var post_time: String? = null,
        var dataApplicant: Applicant? = null
)

data class JobDetails(
        var id: String? = null,
        var uid: String? = null,
        var job_title: String? = null,
        var person_who_post: String? = null,
        var image_url: String? = null,
        var dateStart: String? = null,
        var dateEnd: String? = null,
        var total_day: String? = null,
        var job_description: String? = null,
        var job_category: String? = null,
        var employee_type: String? = null,
        var job_address: String? = null,
        var salary: String? = null,
        var post_time: String? = null
)