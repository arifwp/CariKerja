package com.amikom.carikerja.models

import android.net.Uri

data class Certificate (
    var data: List<CertificateDetails>? = null
)

data class CertificateDetails(
    var id: String? = null,
    var title: String? = null,
    var publishing_organization: String? = null,
    var dateStart: String? = null,
    var expiration_date: String? = null,
    var credential_id: String? = null,
    var credential_url: String? = null,
    var image: Uri? = null,
)

data class CertificateDetailString(
    var id: String? = null,
    var title: String? = null,
    var publishing_organization: String? = null,
    var dateStart: String? = null,
    var expiration_date: String? = null,
    var credential_id: String? = null,
    var credential_url: String? = null,
    var image: String? = null,
)