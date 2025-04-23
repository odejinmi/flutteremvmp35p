package com.a5starcompany.flutteremv.topwise.storage;

import android.util.Log;

import com.a5starcompany.flutteremv.topwise.emv.CardReadResult;


public class ConsumeData {
    private static final String TAG = ConsumeData.class.getSimpleName();

    public String  IPEK_LIVE = "3F2216D8297BCE9C";
    public String KSN_LIVE = "0000000002DDDDE00001";
    public String IPEK_TEST = "9F8011E7E71E483B";
    public String KSN_TEST = "0000000006DDDDE01500";

    public void setIpeklive(String amount) {
        IPEK_LIVE = amount;
    }

    public String getIpeklive() {
        return IPEK_LIVE;
    }

    public void setKsnlive(String amount) {
        KSN_LIVE = amount;
    }

    public String getKsnlive() {
        return KSN_LIVE;
    }
    public static final int CONSUME_TYPE_CARD = 1;
    public static final int CONSUME_TYPE_SCAN = 2;
    public static final int CONSUME_TYPE_CASHBACK = 3;

    public static final int CARD_TYPE_MAG = 100;
    public static final int CARD_TYPE_IC = 101;
    public static final int CARD_TYPE_RF = 102;

    private String mAmount;
    private String mTipAmount;

    private int mConsumeType;
    private int mCardType;

    private String mScanResult;
    private String mCardno;
    private String mSerialNum;
    private String mExpiryData;
    private String mSecondTrackData;
    private String mThirdTrackData;
    private byte[] mPinBlock;
    private byte[] mICData;
    private byte[] mICPositiveData;
    private byte[] mKsnValue;
    private boolean mFallbackTxn;
    private static  ConsumeData instance;

    private CardReadResult cardReadResult;

    public void setCardReadResult(CardReadResult cardReadResult) {
        this.cardReadResult = cardReadResult;
    }

    public CardReadResult getCardReadResult() {
        return cardReadResult;
    }

    private String PINBLOCK;
    public static ConsumeData getInstance() {
        if(instance == null) {
            instance = new ConsumeData();
        }
        return instance;
    }


    private byte[] unifiedPaymentIccData;

    public void setUnifiedPaymentIccData(byte[] unifiedPaymentIccData) {
        this.unifiedPaymentIccData = unifiedPaymentIccData;
    }

    public byte[] getUnifiedPaymentIccData() {
        return this.unifiedPaymentIccData;
    }


    public String getPinBlock() {
        return PINBLOCK;
    }
    public void setPinBlock(String pinBlock) {
        PINBLOCK = pinBlock;
    }
    public boolean ismFallbackTxn() {
        return mFallbackTxn;
    }

    public void setFallbackTxn(boolean fallbackTxn) {
        this.mFallbackTxn = fallbackTxn;
    }

    public byte[] getKsnValue() {
        return mKsnValue;
    }

    public void setKsnValue(byte[] ksnValue) {
        mKsnValue = ksnValue;
    }

    public void setConsumeType(int type) {
        mConsumeType = type;
    }

    public int getConsumeType() {
        return mConsumeType;
    }

    public void setAmount(String amount) {
        mAmount = amount;
    }

    public String getAmount() {
        return mAmount;
    }

    public void setTipAmount(String amount) {
        mTipAmount = amount;
    }

    public String getTipAmount() {
        return mTipAmount;
    }

    public void setCardType(int cardType) {
        mCardType = cardType;
    }

    public int getCardType() {
        return mCardType;
    }

    public void setCardno(String cardno) {
        Log.i(TAG, "setCardno():"+cardno);
        mCardno = cardno;
    }

    public String getCardno() {
        Log.i(TAG, "setCardno():"+mCardno);
        return mCardno;
    }

    public void setExpiryData(String expiryData) {
        mExpiryData = expiryData;
    }

    public String getExpiryData() {
        return mExpiryData;
    }

    public void setSerialNum(String expiryData) {
        mSerialNum = expiryData;
    }

    public String getSerialNum() {
        return mSerialNum;
    }


    public void setSecondTrackData(String secondTrackData) {
        Log.i("lakaladebug", "secondTrackData = "+secondTrackData);
        mSecondTrackData = secondTrackData;
    }

    public String getSecondTrackData() {
        return mSecondTrackData;
    }

    public void setThirdTrackData(String thirdTrackData) {
        Log.i("lakaladebug", "thirdTrackData = "+thirdTrackData);

        mThirdTrackData = thirdTrackData;
    }

    public String getThirdTrackData() {
        return mThirdTrackData;
    }

    public void setPin(byte[] pin) {
        mPinBlock = pin;
    }

    public byte[] getPin() {
        return mPinBlock;
    }

    public void setICData(byte[] icData) {
        mICData = icData;
    }

    public byte[] getICData() {
        return mICData;
    }

    public void setICPositiveData(byte[] icPositiveData) {
        mICPositiveData = icPositiveData;
    }

    public byte[] getICPositiveData() {
        return mICPositiveData;
    }

    public void setScanResult(String scanResult) {
        mScanResult = scanResult;
    }

    public String getScanResult() {
        return mScanResult;
    }

    public void clear() {
        mAmount = null;
        mTipAmount = null;
        mConsumeType = 0;
        mCardType = 0;
        mScanResult = null;
        mCardno = null;
        mSerialNum = null;
        mExpiryData = null;
        mSecondTrackData = null;
        mThirdTrackData = null;
    }
}