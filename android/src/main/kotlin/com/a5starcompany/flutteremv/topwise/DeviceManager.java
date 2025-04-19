package com.a5starcompany.flutteremv.topwise;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.camera.AidlCameraScanCode;
import com.topwise.cloudpos.aidl.emv.AidlPboc;
import com.topwise.cloudpos.aidl.iccard.AidlICCard;
import com.topwise.cloudpos.aidl.led.AidlLed;
import com.topwise.cloudpos.aidl.pinpad.AidlPinpad;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.rfcard.AidlRFCard;
import com.topwise.cloudpos.aidl.shellmonitor.AidlShellMonitor;
import com.topwise.cloudpos.aidl.system.AidlSystem;

/**
 * @author xukun
 * @version 1.0.0
 * @date 18-6-8
 *
 * All Device mode manager ,include Printer ,Pinpad ,
 * IC RF Magnetic card ,Beep.before get the mode handle ,should
 * bind usdk service first .
 */

public class DeviceManager {

    private static String DEVICE_SERVICE_PACKAGE_NAME = "com.android.topwise.topusdkservice";
    private static String DEVICE_SERVICE_CLASS_NAME = "com.android.topwise.topusdkservice.service.DeviceService";
    private static String ACTION_DEVICE_SERVICE = "topwise_cloudpos_device_service";

    private static DeviceManager mDeviceServiceManager;


    private static Context mContext;
    private static AidlDeviceService mDeviceService;

    public   DeviceManager() {
        mContext = TopApp.mPosApp;
    }

    public static DeviceManager getInstance() {
        if (null == mDeviceServiceManager) {
            mDeviceServiceManager = new  DeviceManager();
        }
        return mDeviceServiceManager;
    }

    public   boolean bindService() {
        Log.i("jeremy","");

        Intent intent = new Intent();
        intent.setAction(ACTION_DEVICE_SERVICE);
        intent.setClassName(DEVICE_SERVICE_PACKAGE_NAME, DEVICE_SERVICE_CLASS_NAME);

        try {
            boolean bindResult = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.i("jeremy","bindResult = " + bindResult);
            return bindResult;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void unBindDeviceService() {
        Log.i("jeremy","");

        try {
            mContext.unbindService(mConnection);
        } catch (Exception e) {
            Log.i("jeremy","unbind DeviceService service failed : " + e);
        }
    }

    private static  ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mDeviceService = AidlDeviceService.Stub.asInterface(service);
            Log.i("topwise","onServiceConnected  :  " + mDeviceService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("jeremy","onServiceDisconnected  :  " + mDeviceService);
            mDeviceService = null;
        }
    };

    public IBinder getSystemService() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getSystemService();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlSystem getSystemManager() {

        AidlSystem aidlSystem = AidlSystem.Stub.asInterface(getSystemService());
        return aidlSystem;
    }

    public IBinder getPinPad() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getPinPad(0);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlPinpad getPinpadManager() {

        AidlPinpad aidlPinpad = AidlPinpad.Stub.asInterface(getPinPad());
        return aidlPinpad;
    }

    public AidlLed getLedManager() {

        AidlLed aidlLed = AidlLed.Stub.asInterface(getLed());

        return aidlLed;
    }

    public IBinder getLed() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getLed();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public IBinder getPrinter() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getPrinter();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlPrinter getPrintManager() {

        AidlPrinter aidlPrinter = AidlPrinter.Stub.asInterface(getPrinter());
        return aidlPrinter;
    }

    public IBinder getEMVL2() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getEMVL2();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlPboc getPbocManager() {

        AidlPboc aidlPboc = AidlPboc.Stub.asInterface(getEMVL2());
        return aidlPboc;
    }
    public AidlRFCard getRFCard() {

        AidlRFCard aidlRFCard = AidlRFCard.Stub.asInterface(getRFIDReader());
        return aidlRFCard;
    }

    public IBinder getRFIDReader() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getRFIDReader();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IBinder getPSAMReader(int devid) {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getPSAMReader(devid);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IBinder getSerialPort(int port) {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getSerialPort(port);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlShellMonitor getShellMonitor() {
        try {
            if (mDeviceService != null) {
                return AidlShellMonitor.Stub.asInterface(mDeviceService.getShellMonitor());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IBinder getCPUCard() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getCPUCard();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public IBinder getPedestal() {
        try {
            if (mDeviceService != null) {
                return mDeviceService.getPedestal();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlICCard getICCardMoniter() {
        try {
            if (mDeviceService != null) {
                return AidlICCard.Stub.asInterface(mDeviceService.getInsertCardReader());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlRFCard getRFCardMoniter() {
        try {
            if (mDeviceService != null) {
                return AidlRFCard.Stub.asInterface(mDeviceService.getRFIDReader());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AidlCameraScanCode getScanAidl(){
        try {
            if (mDeviceService != null) {
                return AidlCameraScanCode.Stub.asInterface(mDeviceService.getCameraManager());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }
}
