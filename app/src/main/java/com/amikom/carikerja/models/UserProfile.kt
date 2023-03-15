package com.amikom.carikerja.models

data class UserProfile(
    var uid: String? = null,
    var imageUrl: String? = null,
    var name: String? = null,
    var email: String? = null,
    var role: String? = null,
    var phone: String? = null,
    var dob: String? = null,
    var address: String? = null,
    var summary: String?= null,
    var dataWorkExperience: Exp? = null,
    var certificate: CertificateDetails? = null,
    var project: ProjectDetails? = null,
    var education: EducationDetails? = null
)