package com.amikom.carikerja.models

data class WorkExperience(
    var work: List<Exp>? = null
)

data class Exp(
    var job_title: String? = null,
    var company: String? = null,
    var dateStart: String? = null,
    var dateEnd: String? = null,
    var job_description: String? = null,
    var employee_type: String? = null,
    var job_address: String? = null,
)