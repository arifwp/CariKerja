package com.amikom.carikerja.response

import com.google.gson.annotations.SerializedName

data class InvoiceStatusResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class Data(

	@field:SerializedName("invoiceData")
	val invoiceData: Any? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("senderPhoneNumber")
	val senderPhoneNumber: String? = null,

	@field:SerializedName("notes")
	val notes: String? = null,

	@field:SerializedName("paymentLinkId")
	val paymentLinkId: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("listEnabledBanks")
	val listEnabledBanks: String? = null,

	@field:SerializedName("includeAdminFee")
	val includeAdminFee: Boolean? = null,

	@field:SerializedName("listDisabledPaymentMethods")
	val listDisabledPaymentMethods: String? = null,

	@field:SerializedName("txRefNumber")
	val txRefNumber: String? = null,

	@field:SerializedName("senderName")
	val senderName: String? = null,

	@field:SerializedName("isOpen")
	val isOpen: Boolean? = null,

	@field:SerializedName("phoneNumber")
	val phoneNumber: String? = null,

	@field:SerializedName("expirationTime")
	val expirationTime: String? = null,

	@field:SerializedName("step")
	val step: Any? = null,

	@field:SerializedName("senderNotes")
	val senderNotes: String? = null,

	@field:SerializedName("partnerTxId")
	val partnerTxId: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
