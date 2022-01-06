package com.thaivan.bay.branch;

import android.app.Application;
import android.os.RemoteException;

import com.thaivan.bay.branch.manager.Contextor;

public class MainApplication extends Application {
    private static CardManager cardManager;
    private static SystemManager systemManager;
    private final String TAG = "MainKEY_BATCH_NUMBER_TMSApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.printf("utility:: %s oncreate 00000 \n",TAG);
        Contextor.getInstance().init(this);
        cardManager = CardManager.init(getApplicationContext());
        systemManager = SystemManager.init(getApplicationContext());
        cardManager.bindService();
        systemManager.bindService();
    }

    public static CardManager getCardManager() {
        return cardManager;
    }

    public static SystemManager getSystemManager() {
        return systemManager;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            cardManager.unbindService();
            systemManager.unbindService();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
