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



//    @Headers(
//        "Content-Type: application/json",
//        "Accept: application/json",
//        "x-oy-username: arifwahyu",
//        "x-api-key: 9c3dadc3-6c0b-497f-b8d1-a3b4ef3a2388"
//    )
//    @GET("payment-checkout/{partner_tx_id}")
//    suspend fun getInvoiceStatus(
//        @Path("partner_tx_id") partner_tx_id: String
//    ): Response<InvoiceStatusResponse>

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