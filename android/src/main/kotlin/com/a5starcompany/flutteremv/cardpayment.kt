package com.a5starcompany.flutteremv

import com.a5starcompany.flutteremv.topwise.TopWiseDevice
import com.a5starcompany.flutteremv.topwise.util.StringUtil
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class cardpayment(topWiseDevice : TopWiseDevice,result: MethodChannel.Result, binding: ActivityPluginBinding) {

    private var topWiseDevice: TopWiseDevice? = null
    private var result: MethodChannel.Result? = null
    private var binding: ActivityPluginBinding? = null

    private val TAG = StringUtil.TAGPUBLIC

    init {
        this.topWiseDevice = topWiseDevice
        this.result = result
        this.binding = binding

    }

    /**
     * It is invoked when making transaction
     * @param arg is the data that was passed in from the flutter side to make payment
     */
    fun makePayment(call: MethodCall) {

        if (!(call.arguments is Map<*,*>)) {
            result?.error(ERROR_CODE_PAYMENT_INITIALIZATION, "Invalid input(s)", null)
            return
        }

        val amount = call.argument<String>("amount")!!
        topWiseDevice!!.readCard(amount)
    }

    fun enterpin(call: MethodCall) {

        if (!(call.arguments is Map<*,*>)) {
            result?.error(ERROR_CODE_PAYMENT_INITIALIZATION, "Invalid input(s)", null)
            return
        }
// Convert the string to a ByteArray
        val directpin: String = call.argument<String>("pin")!!
        topWiseDevice!!.enterpin(directpin)
    }

    fun cancelcardprocess() {
        topWiseDevice!!.closeCardReader();
    }

    fun getcardsheme(call: MethodCall) {

        if (!(call.arguments is Map<*,*>)) {
            result?.error(ERROR_CODE_PAYMENT_INITIALIZATION, "Invalid input(s)", null)
            return
        }

        val amount = call.argument<String>("amount")!!
        topWiseDevice!!.getCardScheme(amount)

    }

}