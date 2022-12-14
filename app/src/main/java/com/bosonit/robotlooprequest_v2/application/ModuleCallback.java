package com.bosonit.robotlooprequest_v2.application;

import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.module.ModuleCallbackApi;

public class ModuleCallback extends ModuleCallbackApi {

    private static final String TAG = ModuleCallback.class.getName();


    @Override
    public boolean onSendRequest(int reqId, String reqType, String reqText, String reqParam) throws RemoteException {
        Log.d(TAG, "New request: " + " type is:" + reqType + " text is:" + reqText + " reqParam = " + reqParam);
        String text = "New request: " + " type is:" + reqType + " text is:" + reqText + " reqParam = " + reqParam;

        return true;
    }


    @Override
    public void onHWReport(int function, String type, String message) throws RemoteException {
        Log.i(TAG, "onHWReport function:" + function + " type:" + type + " message:" + message);

    }


    @Override
    public void onSuspend() throws RemoteException {
        Log.d(TAG, "onSuspend");

    }


    @Override
    public void onRecovery() throws RemoteException {
        Log.d(TAG, "onRecovery");

    }
}
