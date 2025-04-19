package com.a5starcompany.flutteremv

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import com.a5starcompany.flutteremv.topwise.TopWiseDevice
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry


const val ERROR_CODE_PAYMENT_INITIALIZATION = "INIT_PAYMENT_ERROR"

class MethodCallHandlerImpl(
    messenger: BinaryMessenger?,
    private val binding: ActivityPluginBinding
) :
    MethodChannel.MethodCallHandler, PluginRegistry.ActivityResultListener,
    PluginRegistry.RequestPermissionsResultListener, EventChannel.StreamHandler{

    private var channel: MethodChannel? = null
    private var result: MethodChannel.Result? = null
    private var eventchannel : EventChannel? = null
    private var eventSink: EventChannel.EventSink? = null


    init {
        channel = MethodChannel(messenger!!, "topwisemp35p")

        channel?.setMethodCallHandler(this)

        eventchannel = EventChannel(messenger, "topwisemp35pevent")
        eventchannel?.setStreamHandler(this)

        binding.addActivityResultListener(this)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        this.result = result
        var cardpayment: cardpayment = cardpayment(topWiseDevice,result,binding)
        var print: print = print(topWiseDevice,binding)

        when (call.method) {

            "initialize" -> {
                Log.d("TAG", "onMethodCall: card listening started")
            }

            "debitcard" -> {
                cardpayment.makePayment(call)
            }

             "enterpin" -> {
                 cardpayment.enterpin(call)
            }

             "cancelcardprocess" -> {
                 cardpayment.cancelcardprocess()
            }

             "getcardsheme" -> {
                 cardpayment.getcardsheme(call)
            }

            "serialnumber" -> {
                val map: MutableMap<String, Any> = mutableMapOf()
                map["state"] = "1"
                map["message"] = serialnumber
                map["status"] = true
                result.success(serialnumber)
            }


            "startprint" -> {
                print.startPrint(call)
            }

            "starteodPrint" -> {
                print.starteodPrint(call)
            }

            "startcustomprint" -> {
                print.startCustomPrint(call)
            }



            else -> {
                result.notImplemented()
            }
        }

    }

    val serialnumber: String
        get() = topWiseDevice.serialnumber;


    val topWiseDevice by lazy {
        TopWiseDevice(binding.activity) {
            var map1: MutableMap<String, Any> = mutableMapOf()
            if (it.transactionData != null) {
                val transactionData = it.transactionData!!

//                val maskedPan = transactionData.applicationPrimaryAccountNumber.let { pan ->
//                    val stars = "*".repeat(pan.length - 9)
//                    pan.take(5) + stars + pan.takeLast(4)
//                }

                map1 = mutableMapOf(
                    "amount" to transactionData.amount,
                    "amountAuthorized" to transactionData.amountAuthorized,
                    "applicationDiscretionaryData" to transactionData.applicationDiscretionaryData,
                    "applicationInterchangeProfile" to transactionData.applicationInterchangeProfile,
                    "applicationIssuerData" to transactionData.applicationIssuerData,
                    "applicationPANSequenceNumber" to transactionData.applicationPANSequenceNumber,
                    "applicationPrimaryAccountNumber" to transactionData.applicationPrimaryAccountNumber,
                    "applicationTransactionCounter" to transactionData.applicationTransactionCounter,
                    "applicationVersionNumber" to transactionData.applicationVersionNumber,
                    "authorizationResponseCode" to transactionData.authorizationResponseCode,
                    "cardHolderName" to transactionData.cardHolderName,
                    "cardScheme" to transactionData.cardScheme,
                    "cardSeqenceNumber" to transactionData.cardSeqenceNumber,
                    "cardholderVerificationMethod" to transactionData.cardholderVerificationMethod,
                    "cashBackAmount" to transactionData.cashBackAmount,
                    "cryptogram" to transactionData.cryptogram,
                    "cryptogramInformationData" to transactionData.cryptogramInformationData,
                    "dedicatedFileName" to transactionData.dedicatedFileName,
                    "deviceSerialNumber" to transactionData.deviceSerialNumber,
                    "dencryptedPinBlock" to transactionData.encryptedPinBlock,
                    "expirationDate" to transactionData.expirationDate,
                    "iccDataString" to transactionData.iccDataString,
                    "interfaceDeviceSerialNumber" to transactionData.interfaceDeviceSerialNumber,
                    "issuerApplicationData" to transactionData.issuerApplicationData,
                    "nibssIccSubset" to transactionData.nibssIccSubset,
                    "originalDeviceSerial" to transactionData.originalDeviceSerial,
                    "originalPan" to transactionData.originalPan,
                    "pinBlock" to transactionData.pinBlock,
                    "pinBlockDUKPT" to transactionData.pinBlockDUKPT,
                    "pinBlockTrippleDES" to transactionData.pinBlockDUKPT,
                    "plainPinKey" to transactionData.plainPinKey,
                    "terminalCapabilities" to transactionData.terminalCapabilities,
                    "terminalCountryCode" to transactionData.terminalCountryCode,
                    "terminalType" to transactionData.terminalType,
                    "terminalVerificationResults" to transactionData.terminalVerificationResults,
                    "track2Data" to transactionData.track2Data,
                    "transactionCurrencyCode" to transactionData.transactionCurrencyCode,
                    "transactionDate" to transactionData.transactionDate,
                    "transactionSequenceCounter" to transactionData.transactionSequenceCounter,
                    "transactionSequenceNumber" to transactionData.transactionSequenceNumber,
                    "transactionType" to transactionData.transactionType,
                    "unifiedPaymentIccData" to transactionData.unifiedPaymentIccData,
                    "unpredictableNumber" to transactionData.unpredictableNumber
                )

            }

            val map: MutableMap<String, Any> = mutableMapOf(
                "state" to it.state.toString(),
                "message" to it.message,
                "status" to it.status,
                "transactionData" to map1
            )
            binding.activity.runOnUiThread {
                eventSink?.success(map)
            }
        }
    }

    /**
     * this is the call back that is invoked when the activity result returns a value after calling
     * startActivityForResult().
     * @param data is the intent that has the bundle where we can get our result [MonnifyTransactionResponse]
     * @param requestCode if it matches with our [REQUEST_CODE] it means the result if the one we
     * asked for.
     * @param resultCode, it is okay if it equals [RESULT_OK]
     */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {

        }
        return true
//
    }

    /**
     * dispose the channel when this handler detaches from the activity
     */
    fun dispose() {
        channel?.setMethodCallHandler(null)
        channel = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        TODO("Not yet implemented")
    }
}