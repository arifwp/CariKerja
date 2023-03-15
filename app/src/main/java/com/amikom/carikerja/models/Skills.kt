package com.amikom.carikerja.models

data class Skills (
    var skill: String? = null,
)

data class DataSkills (
    var dataSkill: List<SkillsDetail>? = null
)

data class SkillsDetail(
    var id: String? = null,
    var skill_name: String? = null
)