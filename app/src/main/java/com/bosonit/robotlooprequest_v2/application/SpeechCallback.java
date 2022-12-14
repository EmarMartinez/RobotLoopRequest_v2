package com.bosonit.robotlooprequest_v2.application;

import android.os.RemoteException;

import com.ainirobot.coreservice.client.speech.SkillCallback;

public class SpeechCallback extends SkillCallback {


    @Override
    public void onSpeechParResult(String s) throws RemoteException {
        System.out.println("SpeechCalllback: onSpeechParResult");
    }

    @Override
    public void onStart() throws RemoteException {
        System.out.println("SpeechCalllback: start");
    }

    @Override
    public void onStop() throws RemoteException {
        System.out.println("SpeechCalllback: stop");
    }

    @Override
    public void onVolumeChange(int i) throws RemoteException {
        System.out.println("SpeechCalllback: volume change");
    }

    @Override
    public void onQueryEnded(int i) throws RemoteException {
        System.out.println("SpeechCalllback: query ended");
    }

    @Override
    public void onQueryAsrResult(String asrResult) throws RemoteException {
        System.out.println("SpeechCalllback: query asr result");
    }
}
