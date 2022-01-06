package com.thaivan.bay.branch;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.centerm.smartpos.util.CompactUtil;
import com.centerm.system.sdk.aidl.IDeviceService;
import com.centerm.system.sdk.aidl.ISystemOperation;
import com.centerm.system.sdk.aidl.SystemFunctionType;

public class SystemManager {

    private ISystemOperation mSystemOperation;
    public static final String SERVICE_ACTION = "com.centerm.smartpos.systemsdk";
    public static final String PACKAGE_NAME = "com.centerm.system.sdk";
    public static SystemManager instance = null;
    private Context context = null;

    public static SystemManager init(Context context) {
        if (instance == null) {
            instance = new SystemManager();
            instance.context = context;
        }

        //  Must enter this few code to make
        //  CompactUtil can call context without null
        if (context != null) {
            //context = instance.getBaseContext();
            CompactUtil.instance(context);
        }

        return instance;
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            if (serviceBinder != null) { // 绑定成功
                try {
                    IDeviceService serviceManager = IDeviceService.Stub.asInterface(serviceBinder);
                    mSystemOperation = ISystemOperation.Stub.asInterface(serviceManager.getSystemOperation());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
//                onDeviceConnected(serviceManager);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    // 绑定服务
    public void bindService() {
        Intent intent = new Intent();
        intent.setPackage(PACKAGE_NAME);
        intent.setAction(SERVICE_ACTION);
//        final Intent eintent = new Intent(createExplicitFromImplicitIntent(this, intent));
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    public void unbindService(){
        context.unbindService(conn);
    }

    public void setHomeKeyEnabled() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(SystemFunctionType.HOME_KEY, true);
        bundle.putBoolean(SystemFunctionType.FUNCTION_KEY, true);
        bundle.putBoolean(SystemFunctionType.STATUS_BAR_KEY, true);
        bundle.putBoolean(SystemFunctionType.POWER_KEY, true);
        try {
//            showMessage(getString(R.string.sys_enable_key));
            mSystemOperation.setSystemFunction(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setHomeKeyDisabled() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(SystemFunctionType.STATUS_BAR_KEY, false);
        bundle.putBoolean(SystemFunctionType.POWER_KEY, true);
        try {
            mSystemOperation.setSystemFunction(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
