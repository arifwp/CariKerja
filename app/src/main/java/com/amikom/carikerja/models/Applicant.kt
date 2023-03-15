package com.amikom.carikerja.models

data class Applicant(
    var uid: String? = null,
    var imageUrl: String? = null,
    var name: String? = null,
    var email: String? = null,
    var role: String? = null,
    var phone: String? = null,
    var dob: String? = null,
    var address: String? = null,
    var summary: String?= null,
    var dataWorkExperience: MutableList<Exp>? = null,
    var certificate: MutableList<CertificateDetailString>? = null,
    var project: MutableList<ProjectDetails>? = null,
    var education: MutableList<EducationDetails>? = null,
    var skills: MutableList<SkillsDetail>? = null
)