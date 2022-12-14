package com.bosonit.robotlooprequest_v2.consumer;

import android.net.Uri;
import android.os.RemoteException;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.ainirobot.coreservice.client.speech.SkillApi;
import com.ainirobot.coreservice.client.speech.entity.TTSEntity;
import com.bosonit.robotlooprequest_v2.MainActivity;
import com.bosonit.robotlooprequest_v2.R;
import com.bosonit.robotlooprequest_v2.application.SpeechCallback;
import com.bosonit.robotlooprequest_v2.model.IncomingOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class OrderConsumer implements Runnable{

    MainActivity ma;

    private SkillApi mSkillApi;
    private SpeechCallback mSkillCallback;
    private ActionListener mNavigationListener;

    boolean finishedLastOrder = true;

    private static final int SLEEP_TIME = 2000;
    public OrderConsumer(MainActivity mainActivity) {
        this.ma = mainActivity;
        this.mSkillCallback = new SpeechCallback();

    }

    @Override
    public void run() {
        setNavigationListener();
        initSkillApi();

        IncomingOrder lastOrder = null;

        System.out.println("OrderConsumer: Iniciando bucle");

        while(true) {
            if(finishedLastOrder) lastOrder = getNextOrder();
            if(lastOrder != null && finishedLastOrder) {
                finishedLastOrder = false;
                System.out.printf("%1$TH:%1$TM:%1$TS%n", System.currentTimeMillis());
                System.out.println("Procesando una orden....");
                processOrder(lastOrder);
            } else {
                try {
                    System.out.println("sleep");
                    Thread.sleep(SLEEP_TIME);
                }
                catch(Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        }

    }

    private void processOrder(IncomingOrder lastOrder) {
        switch (lastOrder.getAction()) {
            case "talk":
                try {
                    System.out.println("ProcessOrder: leyendo...");
                    playText(Objects.requireNonNull(lastOrder.getArguments().get("text")).toString());
                    System.out.println(lastOrder);
                }
                catch(Exception e) {
                    System.out.println("ProcessOrder: (talk) tipo argumento no reconocido");
                    System.out.println("ProcessOrder: " + e.getMessage());
                    finishedLastOrder = true;
                }
                break;
            case "walk":
                double speed;
                double angularSpeed;
                try {
//                    double velocidad = 0.1;
//                    double velocidadAngular = 1.2;
                    speed = Double.parseDouble(Objects.requireNonNull(lastOrder.getArguments().get("speed")).toString());
                    angularSpeed = calcAngularSpeed(speed);

                    RobotApi.getInstance().startNavigation(0, Objects.requireNonNull(lastOrder.getArguments().get("point")).toString(), 1.5, 10 * 1000, speed, angularSpeed, mNavigationListener);
//                    RobotApi.getInstance().startNavigation(0, Objects.requireNonNull(lastOrder.getArguments().get("point")).toString(), 1.5, 10 * 1000, mNavigationListener);

                    System.out.println(Objects.requireNonNull(lastOrder.getArguments().get("point")).toString());
//                    System.out.println("ProcessOrder: En movimiento..... Speed: " + speed + " angularSpeed: " + angularSpeed);

                }
                catch(Exception e) {
                    System.out.println("ProcessOrder: (walk) tipo de argumento no reconocido");
                    System.out.println("ProcessOrder: " + e.getMessage());
                    finishedLastOrder = true;
                }
                break;
            case "wait":
                try {
                    int time;
                    System.out.println("ProcessOrder: esperando...");
                    time = Integer.parseInt(Objects.requireNonNull(lastOrder.getArguments().get("time")).toString());
                    System.out.println(lastOrder);
                    Thread.sleep(time * 1000L);
                    finishedLastOrder = true;
                }
                catch(Exception e) {
                    System.out.println("ProcessOrder: (wait) tipo argumento no reconocido");
                    System.out.println("ProcessOrder: " + e.getMessage());
                    finishedLastOrder = true;
                }
                break;
            case "video":
                try {
                    String name;
                    System.out.println("ProcessOrder: reproduciendo video...");
                    name = (Objects.requireNonNull(lastOrder.getArguments().get("name")).toString());
                    System.out.println(lastOrder);
                    playVideo(name);
                }
                catch(Exception e) {
                    System.out.println("ProcessOrder: (video) argumento incorrecto");
                    System.out.println("ProcessOrder: " + e.getMessage());
                    finishedLastOrder = true;
                }
                break;
            default:
                System.out.println("ProcessOrder: default-> parámetros no reconocidos");
                finishedLastOrder = true;
                break;
        }
    }

    private IncomingOrder getNextOrder() {
        IncomingOrder order;
        try {
            URL url = new URL("http://192.168.10.53:80/api/v1/order/pending_orders");//elliot
//            URL url = new URL("http://192.168.1.58:8080/api/v1/order/pending_orders");//mi laptop
            URLConnection urlConnection = url.openConnection();

            Reader r = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(r);
            String linea;

            while ((linea = br.readLine()) != null) {
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();
                if (linea.trim().isEmpty()) {
                    return null;
                }
                System.out.println("Json recibido:");
                System.out.println(linea);
                order = gson.fromJson(linea, IncomingOrder.class);
                System.out.println(order);
                return order;
            }
            return null;
        }
        catch(Exception e) {
            System.out.println("Catch de getNextOrder");
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void initSkillApi() {
        mSkillApi = new SkillApi();
        ApiListener apiListener = new ApiListener() {
            @Override
            public void handleApiDisabled() {
                System.out.println("SkillApi: apiDisabled");
            }

            @Override
            public void handleApiConnected() {
                mSkillApi.registerCallBack(mSkillCallback);
                System.out.println("SkillApi: apiConnected");
            }

            @Override
            public void handleApiDisconnected() {
                System.out.println("SkillApi: apiDisconnected");
            }
        };
        mSkillApi.addApiEventListener(apiListener);
        mSkillApi.connectApi(ma);
    }

    private void playText(String text) {
        System.out.println("PlayText...");
        if (mSkillApi != null) {
            mSkillApi.playText(new TTSEntity("sid-1234567890", text), new TextListener() {
                @Override
                public void onStart() {
                    System.out.println("TextListener: onStart");
                }
                @Override
                public void onStop() {
                    System.out.println("TextListener: onStop");
                }
                @Override
                public void onError() {
                    System.out.println("TextListener: onError");
                }
                @Override
                public void onComplete() {
                    System.out.println("TextListener: onComplete");
                    finishedLastOrder = true;
                }
            });
        }
    }

    private void stopTTS(){
        if(mSkillApi != null){
            mSkillApi.stopTTS();
        }
    }

    private void queryByText(String text){
        if(mSkillApi != null){
            mSkillApi.queryByText(text);
        }
    }

    private void setNavigationListener() {
        this.mNavigationListener = new ActionListener() {
            @Override
            public void onResult(int status, String response, String extraData) throws RemoteException {
                switch (status) {
                    case Definition.RESULT_OK:
                        if ("true".equals(response)) {
                            System.out.println("NavigationListener: RESULT_OK");
                            finishedLastOrder = true;
                        } else {
                            System.out.println("NavigationListener: RESULT NOT OK");
                        }
                        break;
                }
            }
            @Override
            public void onError(int errorCode, String errorString, String extraData) throws RemoteException {
                switch (errorCode) {
                    case Definition.ERROR_NOT_ESTIMATE:
                        System.out.println("NavigationListener: Error not estimate");
                        break;
                    case Definition.ERROR_IN_DESTINATION:
                        finishedLastOrder = true;
                        System.out.println("NavigationListener: Already within the range");
                        break;
                    case Definition.ERROR_DESTINATION_NOT_EXIST:
                        System.out.println("NavigationListener: Destination doesnt exist");
                        break;
                    case Definition.ERROR_DESTINATION_CAN_NOT_ARRAIVE:
                        System.out.println("NavigationListener: timeout, cannot reach destination");
                        break;
                    case Definition.ACTION_RESPONSE_ALREADY_RUN:
                        System.out.println("NavigationListener: action already in progress");
                        break;
                    case Definition.ACTION_RESPONSE_REQUEST_RES_ERROR:
                        System.out.println("NavigationListener: ACTION_RESPONSE_REQUEST_RES_ERROR");
                        break;
                    case Definition.ERROR_MULTI_ROBOT_WAITING_TIMEOUT:
                        System.out.println("NavigationListener: waiting for another robot timeout");
                        break;
                    case Definition.ERROR_NAVIGATION_FAILED:
                        System.out.println("NavigationListener: navigation failed");
                        break;
                }
            }
            @Override
            public void onStatusUpdate(int status, String data, String extraData) throws RemoteException {
                switch (status) {
                    case Definition.STATUS_NAVI_AVOID:
                        System.out.println("NavigationListener: blocked by obstacles");
                        break;
                    case Definition.STATUS_NAVI_AVOID_END:
                        System.out.println("NavigationListener: obstacle disapeared");
                        break;
                    case Definition.STATUS_START_NAVIGATION:
                        System.out.println("NavigationListener: starting navigation");
                        break;
                    case Definition.STATUS_START_CRUISE:
                        System.out.println("NavigationListener: starting cruise");
                        break;
                    case Definition.STATUS_NAVI_OUT_MAP:
                        System.out.println("NavigationListener: out of map");
                        break;
                    case Definition.STATUS_NAVI_MULTI_ROBOT_WAITING:
                        System.out.println("NavigationListener: waiting for other robot");
                        break;
                    case Definition.STATUS_NAVI_MULTI_ROBOT_WAITING_END:
                        System.out.println("NavigationListener: stopped waiting for another robot");
                        break;
                    case Definition.STATUS_NAVI_GO_STRAIGHT:
                        System.out.println("NavigationListener: moving straight");
                        break;
                    case Definition.STATUS_NAVI_TURN_LEFT:
                        System.out.println("NavigationListener: turning left");
                        break;
                    case Definition.STATUS_NAVI_TURN_RIGHT:
                        System.out.println("NavigationListener: turning right");
                        break;
                }
            }
        };
    }

    private double calcAngularSpeed(double linearSpeed) {
        return 0.4 + (linearSpeed - 0.1) / 3 * 4;
    }

    private void playVideo(String name) {
        ma.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (name) {
                    case "videoprueba":
                        String path = "android.resource://" + ma.getPackageName() + "/" + R.raw.videoprueba;
                        ma.video.setVideoURI(Uri.parse(path));
                        ma.video.start();
                        System.out.println("PlayVideo: reproduciendo video");
                        break;
                    default:
                        System.out.println("Nombre de video no encontrado");
                        break;
                }
            }
        });


    }


}
