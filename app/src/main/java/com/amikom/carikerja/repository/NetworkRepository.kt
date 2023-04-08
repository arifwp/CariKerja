package com.amikom.carikerja.repository

import com.amikom.carikerja.api.ApiService
import com.amikom.carikerja.models.Message
import com.amikom.carikerja.response.InvoiceStatusResponse
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class NetworkRepository @Inject constructor(private val apiService: ApiService) {

//    suspend fun getInvoiceStatus(partner_tx_id: String): Response<InvoiceStatusResponse> = apiService.getInvoiceStatus(partner_tx_id)
    suspend fun postNotification(message: Message): Response<ResponseBody> = apiService.postNotification(message)

}