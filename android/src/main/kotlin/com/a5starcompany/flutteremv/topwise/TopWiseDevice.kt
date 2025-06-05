package com.a5starcompany.flutteremv.topwise

import android.content.Context
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import com.a5starcompany.flutteremv.topwise.app.PosApplication
import com.a5starcompany.flutteremv.topwise.emv.CardReadResult
import com.a5starcompany.flutteremv.topwise.emv.CardReadState
import com.a5starcompany.flutteremv.topwise.emv.Processor
import com.a5starcompany.flutteremv.topwise.emv.TransactionMonitor
import com.a5starcompany.flutteremv.topwise.printer.PrintTemplate
import com.a5starcompany.flutteremv.topwise.util.BCDASCII
import com.a5starcompany.flutteremv.topwise.util.Encrypt
import com.a5starcompany.flutteremv.topwise.util.Format
import com.a5starcompany.flutteremv.topwise.util.HexUtil
import com.topwise.cloudpos.aidl.emv.AidlCheckCardListener
import com.topwise.cloudpos.aidl.emv.AidlPboc
import com.topwise.cloudpos.aidl.emv.EmvTransData
import com.topwise.cloudpos.aidl.iccard.AidlICCard
import com.topwise.cloudpos.aidl.magcard.TrackData
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad
import com.topwise.cloudpos.aidl.printer.AidlPrinter
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard
import com.topwise.cloudpos.data.PinpadConstant

class TopWiseDevice(val context: Context, val callback: (TransactionMonitor) -> Unit, val pincallback : ( Map<String, String>) -> Unit) {

    private val printManager: AidlPrinter? = DeviceManager().getPrintManager()
    val SEARCH_CARD_TIME: Int = 30000

    var aidlICCard: AidlICCard? = null
    var aidlRFCard: AidlRFCard? = null
    init {

        aidlICCard = DeviceManager.getInstance().getICCardMoniter()
        aidlRFCard = DeviceManager.getInstance().getRFCard()
        downLoadKeys()
    }
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
//        PosApplication.getApp().mConsumeData?.pinBlock = BCDASCII.bytesToHexString(
//            Format.pinblock(
//                mCardNo,
//                directpin
//            )
//        )
    PosApplication.getApp().mConsumeData?.pinBlock = Encrypt().encrypt(directpin)

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

//        CardManager.instance.setImportPin(directpin)
        aidlPboc!!.importPin(HexUtil.bcd2str(pin))
    }

    private val mCheckCard by lazy { DeviceManager.getInstance().getPbocManager() }
    val searchCardTime: Int
        get() = 30000

    fun closeCardReader() {
        cancelCheckCard()
    }
    fun readCard(amount: String) {
        println("amount to withdraw $amount");
        PosApplication.getApp().mConsumeData?.amount = amount
        PosApplication.getApp().transactionType = PosApplication.CONSUME
        PosApplication.getApp().processor = Processor.INTERSWITCH
        checkCard()
//        read()
//        return cardConsumeEmitter
    }

    fun getCardScheme(amount: String) {
        PosApplication.getApp().transactionType = PosApplication.CARD_SCHEME
        PosApplication.getApp().mConsumeData?.amount = amount
        PosApplication.getApp().processor = Processor.INTERSWITCH
        checkCard()
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
    private var aidlPboc: AidlPboc? = null
    private var transData: EmvTransData? = null

    val mWorkKeyIndex = 0x00 //work key index
    val mMainKeyIndex = 0x00//main key index

    private fun checkCard() {
        aidlPboc = DeviceManager.getInstance().getPbocManager()

        callback.invoke(TransactionMonitor(
            CardReadState.Loading,
            "card time out",
            true,
            null as CardReadResult?
        ))
        try {
            DeviceManager.getInstance().getRFCard().close()
            DeviceManager.getInstance().getICCardMoniter().close()
            aidlPboc?.checkCard(
                true,
                true,
                true,
                SEARCH_CARD_TIME,
                object : AidlCheckCardListener.Stub() {
                    @Throws(RemoteException::class)
                    override fun onFindMagCard(trackData: TrackData) {
                        println("find mag card")
                        var cardInfo = "Card no" + trackData.getCardno() + "\n"
                        cardInfo += "track expiry data" + trackData.getExpiryDate() + "\n"
                        cardInfo += "track 2 " + trackData.getSecondTrackData() + "\n"
                        cardInfo += "track 3 " + trackData.getThirdTrackData()
                        println("card info " + cardInfo)
                        Log.v(
                            "TAG",
                            "card info " + cardInfo
                        )
                    }

                    @Throws(RemoteException::class)
                    override fun onSwipeCardFail() {
                    }

                    @Throws(RemoteException::class)
                    override fun onFindICCard() {
                        transData = EmvTransData(
                            0x00.toByte(),
                            0x01.toByte(), true, false, false,
                            0x01.toByte(), 0x00.toByte(), byteArrayOf(0x00, 0x00, 0x00)
                        )
                        aidlPboc!!.processPBOC(transData, EmvListener(
                            aidlPboc = aidlPboc!!
                        ){
                            callback.invoke(it)
                        })
                    }

                    @Throws(RemoteException::class)
                    override fun onFindRFCard() {
                        transData = EmvTransData(
                            0x00.toByte(),
                            0x01.toByte(), true, false, false,
                            0x02.toByte(), 0x01.toByte(), byteArrayOf(0x00, 0x00, 0x00)
                        )
                        callback.invoke(TransactionMonitor(
                            CardReadState.Loading,
                            "card time out",
                            true,
                            null as CardReadResult?
                        ))
                        aidlPboc!!.processPBOC(transData, EmvListener(
                            aidlPboc = aidlPboc!!
                        ){
                            callback.invoke(it)
                        })
                    }

                    @Throws(RemoteException::class)
                    override fun onTimeout() {
                        callback.invoke(TransactionMonitor(
                            CardReadState.CardReadTimeOut,
                            "card time out ",
                            true,
                            null as CardReadResult?
                        ))
                    }

                    @Throws(RemoteException::class)
                    override fun onCanceled() {
                        callback.invoke(TransactionMonitor(
                            CardReadState.CallBackCanceled,
                            "card canceled ",
                            true,
                            null as CardReadResult?
                        ))
                    }

                    @Throws(RemoteException::class)
                    override fun onError(error: Int) {
                        aidlPboc!!.cancelCheckCard()
                        Log.v("TAG", "onError  " + error)
                        callback.invoke(TransactionMonitor(
                            CardReadState.CallBackError,
                            "card error $error",
                            true,
                            null as CardReadResult?
                        ))
//                        if (AidlErrorCode.EMV.ERROR_CHECK_ICCARD_RESET_ERROR == error) {
//                            println("go to fallback")
//                            go2Fallback()
//                            /* new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        SystemClock.sleep(500);
//                                        Log.v(TAG,"onError  "+ "Begin Search card ");
//                                        showResult("go to fallback");
//                                        go2Fallback();
//                                    }
//                                }).start();*/
//                        }
                        //  showResult("check card error "+error);
                    }
                })
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun downLoadKeys() {
        val pinpadManager = DeviceManager.getInstance().getPinpadManager()
        val tmk = BCDASCII.hexStringToBytes("89F8B0FDA2F2896B9801F131D32F986D89F8B0FDA2F2896B")
        val tak = BCDASCII.hexStringToBytes("92B1754D6634EB22")
        val tpk = BCDASCII.hexStringToBytes("B5E175AC5FD8DD8A03AD23A35C5BAB6B")
        val trk = BCDASCII.hexStringToBytes("744185122EEC284830694CAD383B4F7A")
        var mIsSuccess = false
        try {
            mIsSuccess = pinpadManager.loadMainkey(mMainKeyIndex, tmk, null)

            mIsSuccess = pinpadManager.loadWorkKey(
                PinpadConstant.WKeyType.WKEY_TYPE_MAK,
                mMainKeyIndex,
                mWorkKeyIndex,
                tak,
                null
            )

            mIsSuccess = pinpadManager.loadWorkKey(
                PinpadConstant.WKeyType.WKEY_TYPE_PIK,
                mMainKeyIndex,
                mWorkKeyIndex,
                tpk,
                null
            )

            mIsSuccess = pinpadManager.loadWorkKey(
                PinpadConstant.WKeyType.WKEY_TYPE_TDK,
                mMainKeyIndex,
                mWorkKeyIndex,
                trk,
                null
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        if (mIsSuccess) {
            println("Download Keys Success")
        } else {
            println("Download Keys Success Fail")
        }
    }

    fun mygetpin(){

        val pinType: Byte
        /*******online pin  */
//        if (type == 0x03) {
//            pinType = 0x00
//        } else {
            pinType = 0x01
//        }
        val bundle: Bundle = getPinParam(pinType)
        var aidlPin : AidlPinpad = DeviceManager.getInstance().getPinpadManager()
        aidlPin.getPin(bundle, MyGetPinListener(aidlPboc!!){
                pincallback.invoke(it)
        })
    }

    /********
     * wkeyid :pin key index;
     * keytype: pin type 0x01== offline pin ;0x00 = online pin
     * input_pin_mode :pin  mode   0,4,5,6  mean the  pin len will be 0,4,5,6
     * if you want to disable bypass ,0 should not  in the  string
     * pan :card no
     * the more param pls see document  p28
     */
    fun getPinParam(pinType: Byte): Bundle {
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
}