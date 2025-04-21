package com.a5starcompany.flutteremv.topwise

import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import com.a5starcompany.flutteremv.topwise.app.PosApplication
import com.a5starcompany.flutteremv.topwise.emv.CardReadResult
import com.a5starcompany.flutteremv.topwise.emv.CardReadState
import com.a5starcompany.flutteremv.topwise.emv.Processor
import com.a5starcompany.flutteremv.topwise.emv.TransactionMonitor
import com.a5starcompany.flutteremv.topwise.util.BCDASCII
import com.a5starcompany.flutteremv.topwise.util.DukptHelper
import com.a5starcompany.flutteremv.topwise.util.Format
import com.a5starcompany.flutteremv.topwise.util.IPEK_LIVE
import com.a5starcompany.flutteremv.topwise.util.KSN_LIVE
import com.topwise.cloudpos.aidl.emv.AidlPboc
import com.topwise.cloudpos.aidl.emv.AidlPbocStartListener
import com.topwise.cloudpos.aidl.emv.CardInfo
import com.topwise.cloudpos.aidl.emv.PCardLoadLog
import com.topwise.cloudpos.aidl.emv.PCardTransLog
import com.topwise.cloudpos.data.LedCode
import java.text.SimpleDateFormat
import java.util.Date

class EmvListener(val aidlPboc:AidlPboc, val mWorkKeyIndex: Int, val callback: (TransactionMonitor) -> Unit) : AidlPbocStartListener.Stub() {

    private var processor: Processor? = null
    /*******
     * callback 1 requestImportAmount
     * call importAmount notify the emv kernel to continue process
     *
     */
    @Throws(RemoteException::class)
    override fun requestImportAmount(arg0: Int) {
        /*****notice importAmount should transfer
         * correct amount to the emv kernel */
        aidlPboc.importAmount(PosApplication.getApp().mConsumeData.amount)
    }

    /*******
     * callback 2 requestAidSelect
     * if the card have multi-application,in this callback the application should
     * show app-list to  the user ,and select an application, call
     * importAidSelectRes tell emv kernel which application have be selected
     * @param  times
     * @param  arg1 application name list
     */
    @Throws(RemoteException::class)
    override fun requestAidSelect(times: Int, arg1: Array<String?>) {
        println("please choice application")
        var str: String? = ""
        for (i in arg1.indices) {
            str += arg1[i]
        }
        aidlPboc.importAidSelectRes(0x01)
    }

    /*******
     * callback 3 requestAidSelect
     * call importFinalAidSelectRes notify the emv kernel to continue process
     * in this callback  ,can set terminal param like show below
     */
    @Throws(RemoteException::class)
    override fun finalAidSelect() {
        println("finalAidSelect")
        aidlPboc.setTlv("9F1A", BCDASCII.hexStringToBytes("0360"))
        aidlPboc.setTlv("5F2A", BCDASCII.hexStringToBytes("0360"))
        aidlPboc.setTlv("9f3c", BCDASCII.hexStringToBytes("0360"))

        aidlPboc.importFinalAidSelectRes(true)
    }

    /*******
     * callback 4 onConfirmCardInfo
     * in onConfirmCardInfo，show card No. to user  if  necessary,
     * and  call importConfirmCardInfoRes notify the emv kernel to
     * continue process after Confirm Card No
     * @param  cardInfo the card info see document
     */
    @Throws(RemoteException::class)
    override fun onConfirmCardInfo(cardInfo: CardInfo) {
        PosApplication.getApp().mConsumeData.cardno = cardInfo.cardno
//        callback.invoke("getString(R.string.card_info)" + cardInfo.cardno)
//        callback.invoke("getString(R.string.please_confirm)")
        aidlPboc.importConfirmCardInfoRes(true)
    }

    /*******
     * callback 4 requestImportPin
     * in requestImportPin,call getPin function to get pin
     * and call importPin notify the emv kernel to continue process
     * @param  type  03== online pin ,others offline pin
     * @param  lastFlag  valid on offline pin , show whether is the last time to input offline Pin
     */
    @Throws(RemoteException::class)
    override fun requestImportPin(type: Int, lastFlag: Boolean, amount: String?) {
        callback.invoke(TransactionMonitor(
            CardReadState.CardDetected,
            PosApplication.getApp().mConsumeData.cardno,
            true,
            null as CardReadResult?
        ))
//        val pinType: Byte
        /*******online pin  */
//        if (type == 0x03) {
//            pinType = 0x00
//        } else {
//            pinType = 0x01
//        }
//        val bundle: Bundle = getPinParam(pinType)
//        var aidlPin : AidlPinpad = DeviceManager.getInstance().getPinpadManager()
//        aidlPin.getPin(bundle, MyGetPinListener(aidlPboc){
//            if (it["state"] == "confirm"){
//                PosApplication.getApp().mConsumeData.pin = BCDASCII.hexStringToBytes(it["message"])
//                PosApplication.getApp().mConsumeData.pinBlock = BCDASCII.bytesToHexString(
//                    Format.pinblock(
//                        PosApplication.getApp().mConsumeData.cardno,
//                        it["message"]
//                    )
//                )
//                /***call importPin notify the emv kernel to continue process  */
//                aidlPboc.importPin(HexUtil.bcd2str(BCDASCII.hexStringToBytes(it["message"])))
//            }else {
//                callback.invoke("callback from mygetpinlistener $it")
//            }
//        })
    }


    /****************
     * if EMV kernel request online process, onRequestOnline will be call .
     * Then the application
     * should call method getTlv to get the EMV tags, then send request message to the host. After host
     * response, the application should call importOnlineResp to notify the EMV kernel
     * to do the second aut
     * @throws RemoteException
     */
    @Throws(RemoteException::class)
    override fun onRequestOnline() {
        println("getString(R.string.request_online)")

        /*******get  EMV tags  here  */
        val seqNum: String? = getSeqNum()
        println("seqNum " + seqNum)
        val track2: String = getTrack2()
        println("track2 " + track2)
        val filed55Data: String? = getConsume55()
        println("filed55Data " + filed55Data)


        /** */
        //send the iso8583 data to host here
        /** */
        /*******
         * after receive the data from the host ,call importOnlineResp;
         * importOnlineResp
         * param 1 : trans result from host
         * param 2: 39 filed the result from host
         * param 3: 55 filed, the script  from host
         */
        aidlPboc.importOnlineResp(true, "00", "")


        processor = PosApplication.getApp().processor
        setConsumePositive55()



        /* isOnline = true;
        if (!isGetPin) {

            Bundle bundle = new Bundle();
            bundle.putBoolean("online", true);
            CardManager.getInstance().startActivity(mContext, bundle, PinpadActivity.class);
        } else {
            //socket通信
            Bundle bundle = new Bundle();
            bundle.putInt(PacketProcessUtils.PACKET_PROCESS_TYPE, PacketProcessUtils.PACKET_PROCESS_CONSUME);
            CardManager.getInstance().startActivity(mContext, bundle, PacketProcessActivity.class);
            Log.d(TAG, "onRequestOnline()");

        */
        /*byte[] sendData = PosApplication.getApp().mConsumeData.getICData();
        Log.d(TAG, BCDASCII.bytesToHexString(sendData));
        JsonAndHttpsUtils.sendJsonData(mContext, BCDASCII.bytesToHexString(sendData));*/
        /*
        }*/
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        val transmissionDate = sdf.format(Date())
        val cardReadResult: CardReadResult = CardReadResult()
        Log.d("TAG", "onRequestOnline: " + processor.toString())
        cardReadResult.applicationTransactionCounter = getApplicationTransactionCounter()
        cardReadResult.cryptogram = getCryptogram()
        cardReadResult.cryptogramInformationData = getCryptogramInformationData()
        cardReadResult.cardholderVerificationMethod = getCVMResult()
        cardReadResult.issuerApplicationData = getIAD()
        cardReadResult.terminalVerificationResults = getTerminalVerificationResult()
        cardReadResult.terminalType = getTerminalType()
        cardReadResult.amount = PosApplication.getApp().mConsumeData.amount
        cardReadResult.amountAuthorized = getAmountAuthorized()
        cardReadResult.applicationVersionNumber = getApplicationVersionNumber()
        getAcquirerIdentifier()
        vfd55()
        cardReadResult.transactionSequenceCounter = getTransactionSequenceCounter()
        cardReadResult.transactionDate = getTransactionDate()
        cardReadResult.transactionType = getTransactionType()
        cardReadResult.unpredictableNumber = getUnpredictedNumner()
        cardReadResult.interfaceDeviceSerialNumber = getInterfaceDeviceSerialNumber()
        cardReadResult.cardHolderName = BCDASCII.hexToAscii(getCardHolderName())
        cardReadResult.applicationInterchangeProfile =getApplicationInterchangeProfile()
        cardReadResult.dedicatedFileName = getDedicatedFileName()

        cardReadResult.terminalCapabilities = getTerminalCapability()
        cardReadResult.terminalCountryCode = getTerminalCountryCode()
        cardReadResult.cashBackAmount = getAmountOther()
        cardReadResult.transactionCurrencyCode = getTransactionCurrencycode()


        cardReadResult.applicationIssuerData = getAid()
        cardReadResult.applicationPrimaryAccountNumber = PosApplication.getApp().mConsumeData.getCardno()
        cardReadResult.expirationDate = setExpired()
        cardReadResult.track2Data = setTrack2()
        Log.d(
            "TAG",
            "track2: " + setTrack2()
        )
        Log.d(
            "TAG",
            "service code: " + extractServiceCode(setTrack2())
        )
        Log.d(
            "TAG",
            "acquiringInstitutionalCode: " + setTrack2()
                .substring(0, 6)
        )
        cardReadResult.cardSeqenceNumber = setSeqNum()
        cardReadResult.iccDataString = BCDASCII.bytesToHexString(setConsume55())
        cardReadResult.unifiedPaymentIccData = BCDASCII.bytesToHexString(getUnifiedPaymentConsume55())
        cardReadResult.pinBlockDUKPT =
            DukptHelper.DesEncryptDukpt(
                DukptHelper.getSessionKey(IPEK_LIVE, KSN_LIVE),
                PosApplication.getApp().mConsumeData.getCardno(),
                BCDASCII.bytesToHexString(PosApplication.getApp().mConsumeData.pin)
            )

        cardReadResult.plainPinKey =
            BCDASCII.bytesToHexString(
                Format.pinblock(
                    cardReadResult.applicationPrimaryAccountNumber,
                    BCDASCII.bytesToHexString(PosApplication.getApp().mConsumeData.pin)
                )
            )

        cardReadResult.pinBlock = PosApplication.getApp().mConsumeData.getPinBlock()
//        PosApplication.getApp().mConsumeData.setCardReadResult(cardReadResult)
//        CardManager.Companion.getInstance().setCardReadResult(cardReadResult)

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

    /**********
     * onTransResult the final trans result
     * @param arg0
     * 0x01 : trans allow
     * 0x02 : trans refuse
     * 0x03 : trans stop
     * 0x04 : trans Downgrade
     * @throws RemoteException
     */
    @Throws(RemoteException::class)
    override fun onTransResult(arg0: Int) {
        DeviceManager.getInstance().getRFCard().close()
        closeLed()
        when (arg0) {
            0x01 -> println("getString(R.string.allow_trading)")
            0x02 -> println("getString(R.string.Refuse_to_deal)")
            0x03 -> println("getString(R.string.stop_trading)")
            0x04 -> println("getString(R.string.downgrade)")
            0x05, 0x06 -> println("getString(R.string.unknown_exception)")
            else -> println("getString(R.string.unknown_exception)")
        }
    }


    /**************
     * ignore this callback
     */
    @Throws(RemoteException::class)
    override fun requestUserAuth(certType: Int, certno: String?) {
        println("requestUserAuth")
        aidlPboc.importUserAuthRes(true)
    }

    /**************
     * ignore this callback
     */
    @Throws(RemoteException::class)
    override fun requestTipsConfirm(arg0: String?) {
        println("requestTipsConfirm")
        aidlPboc.importMsgConfirmRes(true)
    }


    /**************
     * ignore this callback
     */
    @Throws(RemoteException::class)
    override fun requestEcashTipsConfirm() {
        println("requestEcashTipsConfirm")
        aidlPboc.importECashTipConfirmRes(false)
    }

    /**************
     * ignore this callback
     */
    @Throws(RemoteException::class)
    override fun onReadCardTransLog(arg0: Array<PCardTransLog?>?) {
    }

    /**************
     * ignore this callback
     */
    @Throws(RemoteException::class)
    override fun onReadCardOffLineBalance(
        arg0: String?, arg1: String?, arg2: String?,
        arg3: String?
    ) {
    }

    /**************
     * ignore this callback
     */
    @Throws(RemoteException::class)
    override fun onReadCardLoadLog(arg0: String?, arg1: String?, arg2: Array<PCardLoadLog?>?) {
    }

    @Throws(RemoteException::class)
    override fun onError(arg0: Int) {
        println("onError $arg0")
        Log.v("TAG", "onError $arg0")
        aidlPboc.endPBOC()
        aidlPboc.cancelCheckCard()
    }


    private fun getSeqNum(): String? {
        Log.i("TAG", "getSeqNum()")
        val seqNumTag:Array<String?> = arrayOf("5F34")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String? = null

        if (seqNumTlvList != null) {
            val hexString = BCDASCII.bytesToHexString(seqNumTlvList)
            // Ensure at least 2 characters to safely get last two digits
            if (hexString.length >= 2) {
                cardSeqNum = hexString.takeLast(2)
            }
        }

        Log.d("TAG", "setSeqNum: $cardSeqNum")
        return cardSeqNum
    }

    private fun getTrack2(): String {
        Log.i("TAG", "getTrack2()")
        val track2Tag:Array<String?> = arrayOf("57")
        val track2TlvList = getTlv(track2Tag)

        if (track2TlvList == null || track2TlvList.size <= 2) {
            Log.e("TAG", "Track2 TLV data is invalid or too short")
            return ""
        }

        val temp = track2TlvList.copyOfRange(2, track2TlvList.size)
        val track2 = processTrack2(BCDASCII.bytesToHexString(temp))
        Log.i("TAG", "track2(): $track2")
        return track2
    }


    private fun processTrack2(track: String): String {
        Log.i("TAG", "processTrack2()")
        val builder = StringBuilder()
        var subStr: String? = null
        var resultStr: String? = null
        for (i in 0 until track.length) {
            subStr = track.substring(i, i + 1)
            if (!subStr.endsWith("F")) {
                /*if(subStr.endsWith("D")) {
                    builder.append("=");
                } else {*/
                builder.append(subStr)
                /*}*/
            }
        }
        resultStr = builder.toString()
        return resultStr
    }

    private fun getConsume55(): String? {
        Log.i("TAG", "getConsume55()")
        /*String[] consume55Tag = new String[]{"9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                "82", "9F1A", "9F03", "9F33", "9F34", "9F35", "9F1E", "84", "9F09",
                "91", "71", "72", "DF32", "DF33", "DF34"};*/
        val consume55Tag: Array<String?> = arrayOf(
            "4F", "82", "95", "9A", "9B", "9C", "5F24",
            "5F2A", "9F02", "9F03", "9F06", "9F10", "9F12", "9F1A", "9F1C", "9F26",
            "9F27", "9F33", "9F34", "9F36", "9F37", "C2", "CD", "CE", "C0", "C4",
            "C7", "C8"
        )
        val consume55TlvList = getTlv(consume55Tag)
        val filed55 = BCDASCII.bytesToHexString(consume55TlvList)
        Log.d(
            "TAG",
            "setConsume55 consume55TlvList : $filed55"
        )
        return filed55
    }


    private fun getTlv(tags: Array<String?>): ByteArray? {
        val tempList = ByteArray(500)
        var tlvList: ByteArray? = null
        try {
            for (tag in tags) {
                val tempStr = arrayOf<String?>(tag)
                val tempByte = ByteArray(500)
                val len = aidlPboc.readKernelData(tempStr, tempByte)
                Log.d(
                    "TAG",
                    "temp: " + BCDASCII.bytesToHexString(tempByte, len)
                )
            }

            val result = aidlPboc.readKernelData(tags, tempList)

            if (result < 0) {
                return null
            } else {
                tlvList = ByteArray(result)
                System.arraycopy(tempList, 0, tlvList, 0, result)
                Log.i(
                    "TAG",
                    "tlvList: " + BCDASCII.bytesToHexString(tlvList)
                )
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

        return tlvList
    }

    private fun closeLed() {
        val mAidlLed = DeviceManager.getInstance().getLedManager()
        try {
            if (mAidlLed != null) {
                mAidlLed.setLed(LedCode.OPER_LED_ALL, false)
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    /********
     * wkeyid :pin key index;
     * keytype: pin type 0x01== offline pin ;0x00 = online pin
     * input_pin_mode :pin  mode   0,4,5,6  mean the  pin len will be 0,4,5,6
     * if you want to disable bypass ,0 should not  in the  string
     * pan :card no
     * the more param pls see document  p28
     */
    private fun getPinParam(pinType: Byte): Bundle {
        val bundle = Bundle()
        bundle.putInt("wkeyid", mWorkKeyIndex)
        bundle.putInt("keytype", pinType.toInt())
        bundle.putInt("inputtimes", 1)
        bundle.putInt("minlength", 4)
        bundle.putInt("maxlength", 12)
        bundle.putString("pan", "0000000000000000")
        bundle.putString("tips", "RMB:2000.00")
        /*** pin  mode   0,4,5,6  mean the  pin len will be 0,4,5,6
         * if you want to disable bypass ,0 should not  in the  string
         */
        bundle.putString("input_pin_mode", "0,4,5,6")
        return bundle
    }

    private fun setExpired() :String{
        Log.i("TAG", "getExpired()")
        val dataTag: Array<String?> = arrayOf<String?>("5F24")
        val dataTlvList = getTlv(dataTag)
        var expired: String = ""

        if (dataTlvList != null) {
            expired = BCDASCII.bytesToHexString(dataTlvList)
            expired = expired.substring(expired.length - 6, expired.length - 2)
        }
        Log.d("TAG", "setExpired : $expired")
//        PosApplication.getApp().mConsumeData.setExpiryData(expired)
        return expired;
    }

    private fun setSeqNum() :String{
        Log.i("TAG", "getSeqNum()")
        val seqNumTag: Array<String?> = arrayOf<String?>("5F34")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
//            if (processor === Processor.INTERSWITCH) cardSeqNum =
//                cardSeqNum.substring(cardSeqNum.length - 2, cardSeqNum.length)
        }
        Log.d("TAG", "setSeqNum : " + cardSeqNum)
//        PosApplication.getApp().mConsumeData.setSerialNum(cardSeqNum)
        return cardSeqNum
    }

    private fun setTrack2(): String {
        val track2Tag: Array<String?> = arrayOf<String?>("57")
        val track2TlvList: ByteArray = getTlv(track2Tag)!!

        val temp = ByteArray(track2TlvList.size - 2)
        System.arraycopy(track2TlvList, 2, temp, 0, temp.size)
        var track2: String = processTrack2(BCDASCII.bytesToHexString(track2TlvList))
        track2 = track2.substring(4)
//        PosApplication.getApp().mConsumeData.setSecondTrackData(track2)
        Log.i("TAG", "getTrack2() " + track2)
        return track2
    }

    private fun setConsume55():ByteArray {
        Log.i("TAG", "setConsume55()")
        /*String[] consume55Tag = new String[]{"9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                "82", "9F1A", "9F03", "9F33", "9F34", "9F35", "9F1E", "84", "9F09",
                "91", "71", "72", "DF32", "DF33", "DF34"};*/
        val consume55Tag: Array<String?> = arrayOf<String?>(
            "4F", "82", "95", "9A", "9B", "9C", "5F24",
            "5F2A", "9F02", "9F03", "9F06", "9F10", "9F12", "9F1A", "9F1C", "9F26",
            "9F27", "9F33", "9F34", "9F36", "9F37", "C2", "CD", "CE", "C0", "C4",
            "C7", "C8"
        )
        val consume55TlvList: ByteArray = getTlv(consume55Tag)!!
        Log.d(
            "TAG",
            "setConsume55 consume55TlvList : " + BCDASCII.bytesToHexString(consume55TlvList)
        )
//        PosApplication.getApp().mConsumeData.setICData(consume55TlvList)
        return consume55TlvList
    }

    //9F79,
    private fun setConsumePositive55() {
        Log.i("TAG", "getConsumePositive55()")
        val postive55Tag: Array<String?> = arrayOf<String?>("95", "9F1E", "9F10", "9F36")
        val postive55TagTlvList: ByteArray = getTlv(postive55Tag)!!
        Log.d(
            "TAG",
            "setConsume55 postive55TagTlvList : " + BCDASCII.bytesToHexString(postive55TagTlvList)
        )
    }

    private fun getUnifiedPaymentConsume55(): ByteArray {
        Log.i("TAG", "getUnifiedPaymentConsume55()")

        val consume55Tag: Array<String?> = arrayOf<String?>(
            "82",
            "95",
            "9A",
            "9C",
            "5F2A",
            "9F02",
            "9F03",
            "9F10",
            "9F1A",
            "9F26",
            "9F33",
            "9F34",
            "9F35",
            "9F36",
            "9F27",
            "9F37"
        )
        val unifiedPaymentConsume55TlvList: ByteArray = getTlv(consume55Tag)!!
        Log.d(
            "TAG",
            "getUnifiedPaymentConsume55() " + BCDASCII.bytesToHexString(
                unifiedPaymentConsume55TlvList
            )
        )
        return unifiedPaymentConsume55TlvList
    }

    private fun getApplicationTransactionCounter(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F36")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getApplicationTransactionCounter() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getCryptogram(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F26")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getCryptogram() " + cardSeqNum)
        return cardSeqNum
    }


    private fun getCryptogramInformationData(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F27")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getCryptogramInformationData() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getCVMResult(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F34")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }

        Log.i("TAG", "getCVMResult() " + cardSeqNum)

        return cardSeqNum
    }

    private fun getIAD(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F10")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getIAD() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getTerminalVerificationResult(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("95")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getTerminalVerificationResult()) " + cardSeqNum)
        return cardSeqNum
    }

    private fun getTerminalType(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F35")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getTerminalType() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getTransactionDate(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9A")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getTransactionDate() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getTransactionType(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9C")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getTransactionType() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getInterfaceDeviceSerialNumber(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F1E")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getInterfaceDeviceSerialNumber() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getUnpredictedNumner(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F37")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getUnpredictedNumner() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getCardHolderName(): String? {
        val seqNumTag: Array<String?> = arrayOf<String?>("5F20")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String? = null

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getCardHolderName() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getDedicatedFileName(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("84")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getDedicatedFileName() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getAid(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F06")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getAid() " + cardSeqNum)
        return cardSeqNum
    }


    private fun getAmountAuthorized(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F02")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getAmountAuthorized() " + cardSeqNum)
        return cardSeqNum
    }


    //735ED8522707D58A833F17446DD13928
    private fun getTransactionCurrencycode(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("5F2A")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getTransactionCurrencycode() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getTerminalCountryCode(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F1A")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getTerminalCountryCode() " + cardSeqNum)
        return cardSeqNum
    }


    private fun getTerminalCapability(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F33")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getTerminalCapability() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getAmountOther(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F03")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getTerminalCapability() " + cardSeqNum)
        return cardSeqNum
    }


    private fun getApplicationVersionNumber(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F09")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            Log.i("TAG", "getApplicationVersionNumber() full" + cardSeqNum)

            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getApplicationVersionNumber() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getTransactionSequenceCounter(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F41")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            Log.i("TAG", "getTransactionSequenceCounter() full" + cardSeqNum)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getTransactionSequenceCounter() " + cardSeqNum)
        return cardSeqNum
    }


    private fun getApplicationInterchangeProfile(): String {
        val seqNumTag: Array<String?> = arrayOf<String?>("82")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String = ""
        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(4)
        }
        Log.i("TAG", "getApplicationInterchangeProfile() " + cardSeqNum)
        return cardSeqNum
    }

    private fun getAcquirerIdentifier(): String? {
        val seqNumTag: Array<String?> = arrayOf<String?>("9F01")
        val seqNumTlvList = getTlv(seqNumTag)
        var cardSeqNum: String? = null

        if (seqNumTlvList != null) {
            cardSeqNum = BCDASCII.bytesToHexString(seqNumTlvList)
            Log.i("TAG", "getAcquirerIdentifier() full" + cardSeqNum)
            if (processor === Processor.INTERSWITCH) cardSeqNum = cardSeqNum.substring(6)
        }
        Log.i("TAG", "getAcquirerIdentifier() " + cardSeqNum)
        return cardSeqNum
    }

    private fun vfd55() {
        Log.i("TAG", "setConsume55()")
        /*String[] consume55Tag = new String[]{"9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                "82", "9F1A", "9F03", "9F33", "9F34", "9F35", "9F1E", "84", "9F09",
                "91", "71", "72", "DF32", "DF33", "DF34"};*/
//        String[] vfd55 = new String[]{
//                "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A" ,"5F34"
//                , "82", "9F1A", "9F03", "9F33", "84",
//                 "9F34", "9F35", "4F"
//        };
        val vfd55: Array<String?> = arrayOf<String?>( // "9F01",
            "9F02",
            "9F03",
            "9F09",
            "9F10",
            "9F15",
            "9F26",
            "9F27",
            "9F33",
            "9F34",
            "9F35",
            "9F36",
            "9F37",
            "9F41",
            "9F1A",
            "9F1E",
            "95",
            "9A",
            "9C",
            "5F24",
            "5F2A",
            "5F34",
            "82",
            "84"
        )

        //        byte[] consume55TlvList = getTlv(vfd55);
//        Log.d(TAG, "vfd55: " + BCDASCII.bytesToHexString(consume55TlvList));
        val consume55TlvList: ByteArray = getTlv(vfd55)!!
        val filed55 = BCDASCII.bytesToHexString(consume55TlvList)
        Log.d("TAG", "setConsume55 consume55TlvList : " + filed55)

//        ICPbocStartListenerSub.topTool = TopTool.getInstance(mContext)
//        ICPbocStartListenerSub.packer = ICPbocStartListenerSub.topTool.getPacker()
//        ICPbocStartListenerSub.convert = ICPbocStartListenerSub.topTool.getConvert()
//
//
//        val tlv: ITlv = ICPbocStartListenerSub.packer.getTlv()
//        var list: ITlv.ITlvDataObjList? = null
//        try {
//            list = tlv.unpack(consume55TlvList)
//        } catch (e: TlvException) {
//            e.printStackTrace()
//        }
//        // tag 9F10
//        val value9F10: ByteArray? = list.getValueByTag(0x9F10)
//        if (value9F10 != null && value9F10.size > 0) {
//            val temp: String? = ICPbocStartListenerSub.convert.bcdToStr(value9F10)
//            Log.d("TAG", "value9F10 value9F10 : " + temp)
//        }
//
//        val consume9F10Tag: Array<String?> = arrayOf<String?>("9F01")
//        val consume9F10Tags: ByteArray = getTlv(consume9F10Tag)!!
//        val f10Str = BCDASCII.bytesToHexString(consume9F10Tags)
//        Log.d("TAG", "9F01Str 9f01Str : " + f10Str)
//        PosApplication.getApp().mConsumeData.setICData(consume55TlvList)
    }

    private fun extractServiceCode(track2Data: String): String {
        val indexOfToken = track2Data.indexOf("D")
        val indexOfServiceCode = indexOfToken + 5
        val lengthOfServiceCode = 3
        return track2Data.substring(indexOfServiceCode, indexOfServiceCode + lengthOfServiceCode)
    }

}