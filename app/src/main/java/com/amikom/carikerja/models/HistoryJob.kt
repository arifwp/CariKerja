package com.amikom.carikerja.models

data class HistoryJob (
    var id_job: String? = null,
    var job_title: String? = null,
    var recruiter_uid: String? = null,
    var recruiter_name: String? = null,
    var status: String? = null,
    var id_applicant: String? = null,
)