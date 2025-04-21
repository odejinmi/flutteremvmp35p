package com.a5starcompany.flutteremv.topwise

import android.os.RemoteException
import com.a5starcompany.flutteremv.topwise.util.BCDASCII
import com.topwise.cloudpos.aidl.emv.AidlPboc
import com.topwise.cloudpos.aidl.pinpad.GetPinListener
import com.topwise.cloudpos.data.LedCode

class MyGetPinListener(val aidlPboc : AidlPboc, val callback : ( Map<String, String>) -> Unit) : GetPinListener.Stub() {
    @Throws(RemoteException::class)
    override fun onStopGetPin() {
        //showMessage("您取消了PIN输入");
        callback.invoke(
                mapOf(
                    "state" to "stop",
                    "message" to "onStopGetPin"
                )
        )
        aidlPboc.endPBOC()
        aidlPboc.cancelCheckCard()
        closeLed()
    }

    @Throws(RemoteException::class)
    override fun onInputKey(len: Int, arg1: String?) {
        //showMessage(getStar(arg0) + arg1 == null ? "" : arg1);
        callback.invoke(
            mapOf(
                "state" to "inputkey",
                "message" to arg1.toString()
            )
        )
    }

    @Throws(RemoteException::class)
    override fun onError(errorCode: Int) {
        callback.invoke(
            mapOf(
                "state" to "error",
                "message" to errorCode.toString()
            )
        )
        aidlPboc.endPBOC()
        aidlPboc.cancelCheckCard()
        closeLed()
    }

    @Throws(RemoteException::class)
    override fun onConfirmInput(arg0: ByteArray?) {
//        "get Pin " + HexUtil.bcd2str(arg0)
        callback.invoke(
            mapOf(
                "state" to "confirm",
                "message" to BCDASCII.bytesToHexString(arg0)
            )
        )
    }

    @Throws(RemoteException::class)
    override fun onCancelKeyPress() {
        callback.invoke(
            mapOf(
                "state" to "cancel",
                "message" to "OnCancelKeyPress"
            )
        )
        aidlPboc.endPBOC()
        aidlPboc.cancelCheckCard()
        closeLed()
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
}

