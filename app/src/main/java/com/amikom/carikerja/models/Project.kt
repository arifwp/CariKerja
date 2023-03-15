package com.amikom.carikerja.models

data class Project (
    var projectDetails: List<ProjectDetails>? = null
)

data class ProjectDetails(
    var id: String? = null,
    var project_name: String? = null,
    var role: String? = null,
    var dateStart: String? = null,
    var dateEnd: String? = null,
    var description: String? = null,
)