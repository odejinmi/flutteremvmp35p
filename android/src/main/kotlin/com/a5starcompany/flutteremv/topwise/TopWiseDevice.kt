package com.a5starcompany.flutteremv.topwise

import android.content.Context
import android.os.RemoteException
import android.util.Log
import com.a5starcompany.flutteremv.topwise.app.PosApplication
import com.a5starcompany.flutteremv.topwise.emv.TransactionMonitor
import com.a5starcompany.flutteremv.topwise.util.BCDASCII
import com.a5starcompany.flutteremv.topwise.util.Format
import com.a5starcompany.flutteremv.topwise.card.CardManager
import com.a5starcompany.flutteremv.topwise.emv.CardReadResult
import com.a5starcompany.flutteremv.topwise.emv.CardReadState
import com.a5starcompany.flutteremv.topwise.emv.Processor
import com.a5starcompany.flutteremv.topwise.printer.PrintTemplate
import com.topwise.cloudpos.aidl.printer.AidlPrinter
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class TopWiseDevice(val context: Context, val callback: (TransactionMonitor) -> Unit) {

    private val printManager: AidlPrinter? = DeviceManager().getPrintManager()

    fun printDoc(template: PrintTemplate) {
        printManager?.addRuiImage(template.printBitmap, 0);
        printManager?.printRuiQueue(object : AidlPrinterListener.Stub() {
            override fun onError(p0: Int) {
//                printListener.onError(p0)
            }

            override fun onPrintFinish() {
//                printListener.onPrintFinish()
            }

        })
    }

    val serialnumber: String
        get() = DeviceManager().getSystemManager().serialNo!!

    fun enterpin(directpin: String) {

// Convert the string to a ByteArray
        val pin: ByteArray = BCDASCII.hexStringToBytes(directpin)
//        val pin = call.argument<String>("pin")!!
        Log.i("TAG", "onConfirmInput(), pin = " + BCDASCII.bytesToHexString(pin))
        val mCardNo = PosApplication.getApp().mConsumeData?.cardno
//        var finalPan = ""
//        mCardNo?.let {
//            val numbersOfStars =
//                mCardNo.length - (mCardNo.take(5).length + mCardNo.takeLast(4).length)
//            var stars = ""
//            for (i in 1..numbersOfStars)
//                stars += "*"
//            finalPan = mCardNo.take(5) + stars + mCardNo!!.takeLast(4)
//        }
        PosApplication.getApp().mConsumeData?.pin = pin
        PosApplication.getApp().mConsumeData?.pinBlock = BCDASCII.bytesToHexString(
            Format.pinblock(
                mCardNo,
                directpin
            )
        )

//            if (isOnline) {
//                //socket通信
//                val bundle = Bundle()
//                bundle.putInt(
//                    PacketProcessUtils.PACKET_PROCESS_TYPE,
//                    PacketProcessUtils.PACKET_PROCESS_CONSUME
//                )
////                CardManager.getInstance()
////                    .startActivity(this@PinpadActivity, bundle, PacketProcessActivity::class.java)
//                /*byte[] sendData = PosApplication.Companion.getApp().mConsumeData.getICData();
//                Log.d(TAG, BCDASCII.bytesToHexString(sendData));
//                JsonAndHttpsUtils.sendJsonData(mContext, BCDASCII.bytesToHexString(sendData));*/
//            } else {
////                if (ConsumeData.CARD_TYPE_MAG === mCardType) {
////                    //val intent = Intent(this@PinpadActivity, PacketProcessActivity::class.java)
////                    intent.putExtra(
////                        PacketProcessUtils.PACKET_PROCESS_TYPE,
////                        PacketProcessUtils.PACKET_PROCESS_CONSUME
////                    )
////                    startActivity(intent)
////                } else {
////
////                }
//
//                if (pin == null) {
//                    CardManager.instance.setImportPin("000000")
//                } else {
//
//                }
//            }

        CardManager.instance.setImportPin(directpin)
    }

    private val mCheckCard by lazy { DeviceManager.getInstance().getPbocManager() }
    val searchCardTime: Int
        get() = 30000
    val job = Job()
    private val coroutineContext = job + Dispatchers.Main
    private val mainScope = MainScope()

    fun closeCardReader() {
        cancelCheckCard()
        CardManager.instance.stopCardDealService(context)
    }
    fun readCard(amount: String) {
        CardManager.instance.stopCardDealService(context)
        PosApplication.getApp().mConsumeData?.amount = amount
        PosApplication.getApp().transactionType = PosApplication.CONSUME
        PosApplication.getApp().processor = Processor.INTERSWITCH
        read()
//        return cardConsumeEmitter
    }

    fun getCardScheme(amount: String) {
        CardManager.instance.stopCardDealService(context)
        PosApplication.getApp().transactionType = PosApplication.CARD_SCHEME
        PosApplication.getApp().mConsumeData?.amount = amount
        PosApplication.getApp().processor = Processor.INTERSWITCH
        getScheme()
    }

    private fun read() {
//        this.callback.invoke(TransactionMonitor(
//                CardReadState.Loading,
//                "device error ",
//                true,
//                null as CardReadResult?
//        ))
//        cardConsumeEmitter.emit(CardReadState.Loading)
        CardManager.instance.initCardExceptionCallBack(object : CardManager.CardExceptionCallBack {
            override fun callBackTimeOut() {
                callback.invoke(TransactionMonitor(
                    CardReadState.CardReadTimeOut,
                    "card time out ",
                    true,
                    null as CardReadResult?
                ))
            }

            override fun callBackError(errorCode: Int) {
                callback.invoke(TransactionMonitor(
                    CardReadState.CallBackError,
                    "card time out $errorCode",
                    true,
                    null as CardReadResult?
                ))
            }

            override fun callBackCanceled() {
                callback.invoke(TransactionMonitor(
                    CardReadState.CallBackCanceled,
                    "card canceled ",
                    true,
                    null as CardReadResult?
                ))
            }

            override fun callBackTransResult(result: Int) {

            }

            override fun finishPreActivity() {

            }
        })

        CardManager.instance.setCardFoundListener(object : CardManager.Card {
            override fun searching() {
                callback.invoke(TransactionMonitor(
                    CardReadState.Loading,
                    "card time out",
                    true,
                    null as CardReadResult?
                ))
            }

            override fun onCardDetected(pan: String) {
                callback.invoke(TransactionMonitor(
                    CardReadState.CardDetected,
                    pan,
                    true,
                    null as CardReadResult?
                ))
            }

            override fun onCardReadResult(cardReadResult: CardReadResult) {
                val shola =  CardReadResult(
                    issuerApplicationData = cardReadResult.issuerApplicationData,
                    applicationVersionNumber = cardReadResult.applicationVersionNumber,
                    transactionType = cardReadResult.transactionType,
                    amount = cardReadResult.amount,
                    amountAuthorized = cardReadResult.amountAuthorized,
                    cardSeqenceNumber = cardReadResult.cardSeqenceNumber,
                    unifiedPaymentIccData = cardReadResult.unifiedPaymentIccData,
                    deviceSerialNumber = cardReadResult.deviceSerialNumber,
                    iccDataString = cardReadResult.iccDataString,
                    authorizationResponseCode = cardReadResult.authorizationResponseCode,
                    nibssIccSubset = cardReadResult.nibssIccSubset,
                    applicationTransactionCounter = cardReadResult.applicationTransactionCounter,
                    terminalVerificationResults = cardReadResult.terminalVerificationResults,
                    expirationDate = cardReadResult.expirationDate,
                    applicationPrimaryAccountNumber = cardReadResult.applicationPrimaryAccountNumber,
                    applicationPANSequenceNumber = cardReadResult.applicationPANSequenceNumber,
                    transactionDate = cardReadResult.transactionDate,
                    cryptogramInformationData = cardReadResult.cryptogramInformationData,
                    dedicatedFileName = cardReadResult.dedicatedFileName,
                    transactionSequenceNumber = cardReadResult.transactionSequenceNumber,
                    transactionSequenceCounter = cardReadResult.transactionSequenceCounter,
                    cryptogram = cardReadResult.cryptogram,
                    track2Data = cardReadResult.track2Data,
                    cardholderVerificationMethod = cardReadResult.cardholderVerificationMethod,
                    applicationInterchangeProfile = cardReadResult.applicationInterchangeProfile,
                    pinBlockDUKPT = cardReadResult.pinBlockDUKPT,
                    pinBlockTrippleDES = cardReadResult.pinBlockTrippleDES,
                    cardScheme = cardReadResult.cardScheme,
                    applicationDiscretionaryData = cardReadResult.applicationDiscretionaryData,
                    unpredictableNumber = cardReadResult.unpredictableNumber,
                    interfaceDeviceSerialNumber = cardReadResult.interfaceDeviceSerialNumber,
                    encryptedPinBlock = cardReadResult.encryptedPinBlock,
                    terminalType = cardReadResult.terminalType,
                    cardHolderName = cardReadResult.cardHolderName,
                    originalDeviceSerial = cardReadResult.originalDeviceSerial,
                    transactionCurrencyCode = cardReadResult.transactionCurrencyCode,
                    terminalCountryCode = cardReadResult.terminalCountryCode,
                    cashBackAmount = cardReadResult.cashBackAmount,
                    terminalCapabilities = cardReadResult.terminalCapabilities,
                    plainPinKey = cardReadResult.plainPinKey,
                    originalPan = cardReadResult.originalPan,
                )
                callback.invoke(TransactionMonitor(
                    CardReadState.CardData,
                    "card time out",
                    true,
                    shola
                ))
            }
        })
        CardManager.instance.startCardDealService(context)
    }

    private fun getScheme() {
        this.callback.invoke(TransactionMonitor(
            CardReadState.Loading,
            "device error ",
            true,
            null as CardReadResult?
        ))
        CardManager.instance.initCardExceptionCallBack(object : CardManager.CardExceptionCallBack {
            override fun callBackTimeOut() {
                mainScope.launch {
                    callback.invoke(TransactionMonitor(
                        CardReadState.CardReadTimeOut,
                        "card time out ",
                        true,
                        null as CardReadResult?
                    ))
                }
            }

            override fun callBackError(errorCode: Int) {
                callback.invoke(TransactionMonitor(
                    CardReadState.CallBackError,
                    "card time out $errorCode",
                    true,
                    null as CardReadResult?
                ))
            }

            override fun callBackCanceled() {
                callback.invoke(TransactionMonitor(
                    CardReadState.CallBackCanceled,
                    "card canceled",
                    true,
                    null as CardReadResult?
                ))
            }

            override fun callBackTransResult(result: Int) {

            }

            override fun finishPreActivity() {

            }
        })
        CardManager.instance.setCardFoundListener(object : CardManager.Card {
            override fun searching() {
                callback.invoke(TransactionMonitor(
                    CardReadState.Loading,
                    "Loading ",
                    true,
                    null as CardReadResult?
                ))
            }

            override fun onCardDetected(pan: String) {
                callback.invoke(TransactionMonitor(
                    CardReadState.CardDetected,
                    pan,
                    true,
                    null as CardReadResult?
                ))
            }

            override fun onCardReadResult(cardReadResult: CardReadResult) {
                val shola =  CardReadResult(
                    issuerApplicationData = cardReadResult.issuerApplicationData,
                    applicationVersionNumber = cardReadResult.applicationVersionNumber,
                    transactionType = cardReadResult.transactionType,
                    amount = cardReadResult.amount,
                    amountAuthorized = cardReadResult.amountAuthorized,
                    cardSeqenceNumber = cardReadResult.cardSeqenceNumber,
                    unifiedPaymentIccData = cardReadResult.unifiedPaymentIccData,
                    deviceSerialNumber = cardReadResult.deviceSerialNumber,
                    iccDataString = cardReadResult.iccDataString,
                    authorizationResponseCode = cardReadResult.authorizationResponseCode,
                    nibssIccSubset = cardReadResult.nibssIccSubset,
                    applicationTransactionCounter = cardReadResult.applicationTransactionCounter,
                    terminalVerificationResults = cardReadResult.terminalVerificationResults,
                    expirationDate = cardReadResult.expirationDate,
                    applicationPrimaryAccountNumber = cardReadResult.applicationPrimaryAccountNumber,
                    applicationPANSequenceNumber = cardReadResult.applicationPANSequenceNumber,
                    transactionDate = cardReadResult.transactionDate,
                    cryptogramInformationData = cardReadResult.cryptogramInformationData,
                    dedicatedFileName = cardReadResult.dedicatedFileName,
                    transactionSequenceNumber = cardReadResult.transactionSequenceNumber,
                    transactionSequenceCounter = cardReadResult.transactionSequenceCounter,
                    cryptogram = cardReadResult.cryptogram,
                    track2Data = cardReadResult.track2Data,
                    cardholderVerificationMethod = cardReadResult.cardholderVerificationMethod,
                    applicationInterchangeProfile = cardReadResult.applicationInterchangeProfile,
                    pinBlockDUKPT = cardReadResult.pinBlockDUKPT,
                    pinBlockTrippleDES = cardReadResult.pinBlockTrippleDES,
                    cardScheme = cardReadResult.cardScheme,
                    applicationDiscretionaryData = cardReadResult.applicationDiscretionaryData,
                    unpredictableNumber = cardReadResult.unpredictableNumber,
                    interfaceDeviceSerialNumber = cardReadResult.interfaceDeviceSerialNumber,
                    encryptedPinBlock = cardReadResult.encryptedPinBlock,
                    terminalType = cardReadResult.terminalType,
                    cardHolderName = cardReadResult.cardHolderName,
                    originalDeviceSerial = cardReadResult.originalDeviceSerial,
                    transactionCurrencyCode = cardReadResult.transactionCurrencyCode,
                    terminalCountryCode = cardReadResult.terminalCountryCode,
                    cashBackAmount = cardReadResult.cashBackAmount,
                    terminalCapabilities = cardReadResult.terminalCapabilities,
                    plainPinKey = cardReadResult.plainPinKey,
                    originalPan = cardReadResult.originalPan,
                )
                callback.invoke(TransactionMonitor(
                    CardReadState.CardData,
                    "card time out",
                    true,
                    shola
                ))
            }
        })
        CardManager.instance.setCardSchemeListener(object : CardManager.CardSchemeListener {
            override fun onCardScheme(cardScheme: String, maskedPan: String) {
                callback.invoke(TransactionMonitor(
                    CardReadState.CardDetected,
                    "card detected $cardScheme, $maskedPan",
                    true,
                    null as CardReadResult?
                ))
            }

        })
        CardManager.instance.startCardDealService(context)
    }


    private fun cancelCheckCard() {
        synchronized(this) {
            try {
                if (mCheckCard != null) {
                    mCheckCard!!.cancelCheckCard()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }


}