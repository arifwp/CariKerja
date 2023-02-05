package com.amikom.carikerja.models

data class Education (
    var data: List<EducationDetails>? = null
)

data class EducationDetails(
    var institution: String? = null,
    var degree: String? = null,
    var field_of_study: String? = null,
    var dateStart: String? = null,
    var dateEnd: String? = null,
    var description: String? = null,
)