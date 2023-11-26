package com.amikom.carikerja.api

import com.amikom.carikerja.models.Message
import com.amikom.carikerja.response.InvoiceStatusResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @Headers(
        "Authorization: key=AAAAK-WQOKE:APA91bEwAvh5BbO6DrATRGN6zy9dF0QFG26Wd_83JpC-0UcYe4RCieRMfWCScXNJnBIRhAOHHCKGH6D358wC3SVVPBxKEMmv35vBGFaRbdqbe7DLNwOBsjW_1Gyh9DWvlsIeoIPmfXkb",
        "Content-Type: application/json"
    )
    @POST("fcm/send")
    suspend fun postNotification(
        @Body message: Message
    ): Response<ResponseBody>

    companion object {
        const val SERVER_KEY = "AAAAK-WQOKE:APA91bEwAvh5BbO6DrATRGN6zy9dF0QFG26Wd_83JpC-0UcYe4RCieRMfWCScXNJnBIRhAOHHCKGH6D358wC3SVVPBxKEMmv35vBGFaRbdqbe7DLNwOBsjW_1Gyh9DWvlsIeoIPmfXkb"
        const val CONTENT_TYPE = "application/json"
    }

}