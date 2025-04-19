package com.a5starcompany.flutteremv.topwise.card;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.a5starcompany.flutteremv.topwise.DeviceManager;
import com.a5starcompany.flutteremv.topwise.util.StringUtil;
import com.topwise.cloudpos.aidl.emv.AidlPboc;

public class CardMoniterService extends Service {
    private final String TAG = StringUtil.TAGPUBLIC + CardMoniterService.class.getSimpleName();
    private static final int SEARCH_CARD_TIME = 30000;
    private AidlPboc mCheckCard;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        mCheckCard = DeviceManager.getInstance().getPbocManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        checkCard(true, true, true);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    private void checkCard(boolean isMag, boolean isIc, boolean isRf) {
        Log.i(TAG, "searchCard()");
        synchronized (this) {

            try {
                if (mCheckCard != null)
                    mCheckCard.checkCard(isMag, isIc, isRf, SEARCH_CARD_TIME, new CheckCardListenerSub(this));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void cancelCheckCard() {
        Log.i(TAG, "cancelCheckCard()");
        synchronized (this) {
            try {
                if (mCheckCard != null) {
                    mCheckCard.cancelCheckCard();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        cancelCheckCard();
    }
}


