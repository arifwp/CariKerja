package com.amikom.carikerja.utils

import android.content.Context
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

object SdkUiMidtrans {

    fun initSdkUiFlow(context: Context){
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-bLMGyRfhDOmFGBcs")
            .setContext(context)
            .setTransactionFinishedCallback(TransactionFinishedCallback { result ->

            })
            .setMerchantBaseUrl("http://192.168.9.249/response.php/")
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()
    }

}