package com.amikom.carikerja.api

import com.amikom.carikerja.response.InvoiceStatusResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiService {

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "x-oy-username: arifwahyu",
        "x-api-key: 9c3dadc3-6c0b-497f-b8d1-a3b4ef3a2388"
    )
    @GET("payment-checkout/{partner_tx_id}")
    suspend fun getInvoiceStatus(
        @Path("partner_tx_id") partner_tx_id: String
    ): Response<InvoiceStatusResponse>

}